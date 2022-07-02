package com.chen.gulimallorder.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chen.common.constant.RedisConstant;
import com.chen.common.utils.R;
import com.chen.gulimallorder.entity.OrderItemEntity;
import com.chen.gulimallorder.enume.OrderStatusEnum;
import com.chen.gulimallorder.feign.CartFeignService;
import com.chen.gulimallorder.feign.MemberFeignService;
import com.chen.gulimallorder.feign.ProductFeignService;
import com.chen.gulimallorder.feign.WmsFeignService;
import com.chen.gulimallorder.interceptor.AuthorizationInterceptor;
import com.chen.gulimallorder.mq.MyMQConfig;
import com.chen.gulimallorder.service.OrderItemService;
import com.chen.gulimallorder.to.OrderCreateTo;
import com.chen.gulimallorder.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.utils.PageUtils;
import com.chen.common.utils.Query;

import com.chen.gulimallorder.dao.OrderDao;
import com.chen.gulimallorder.entity.OrderEntity;
import com.chen.gulimallorder.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderComfirmVo comfirmOrder() throws ExecutionException, InterruptedException {
        OrderComfirmVo comfirmVo = new OrderComfirmVo();
        Long memberId = AuthorizationInterceptor.userIdThreadLocal.get();
        //设置RequestAttributes可以被子线程继承
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes,true);

        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            //获得会员地址信息
            List<MemberAddressVo> address = memberFeignService.getAddress(memberId);
            comfirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //购物车所有选中的购物项
            List<OrderItemVo> orderItems = cartFeignService.getCurrentUserCartItems();
            comfirmVo.setItems(orderItems);
        }, executor).thenRunAsync(()->{
            List<OrderItemVo> items = comfirmVo.getItems();
            List<Long> skuIds = new ArrayList<>();
            for (OrderItemVo item:items)
                skuIds.add(item.getSkuId());
            R wmsReturn = wmsFeignService.getSkuHasStock(skuIds);
            if(wmsReturn.getCode()==0)
            {
                List<SkuHasStockVo> data = wmsReturn.getData("data", new TypeReference<List<SkuHasStockVo>>() {});
                Map<Long,Boolean> skuHasStockMap = new HashMap<>();
                for(SkuHasStockVo vo:data)
                    skuHasStockMap.put(vo.getSkuId(),vo.getHasStock());
                comfirmVo.setSkuHasStock(skuHasStockMap);
            }else
            {
                //调用失败
                throw new RuntimeException("仓库服务调用异常");
            }
        },executor);

        //获得会员积分
        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            Integer integration = memberFeignService.getIntegration(memberId);
            if (integration != null)
                comfirmVo.setIntegration(integration);
        }, executor);

        //价格

        //TODO 防重令牌
        String orderToken = UUID.randomUUID().toString().replaceAll("-", "");
        String key = RedisConstant.KeyEnum.ORDER_TOKEN_PREFIX.getKey()+":"+
                AuthorizationInterceptor.userIdThreadLocal.get();
        redisTemplate.opsForValue().set(key,orderToken,30, TimeUnit.MINUTES);
        comfirmVo.setOrderToken(orderToken);

        CompletableFuture.allOf(addressFuture,cartFuture,memberFuture).get();
        return comfirmVo;
    }

    @Override
    @Transactional
    public OrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        OrderResponseVo responseVo = new OrderResponseVo();
        //验证token令牌
        //原子验证以及删令牌
        String lua = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String key =  RedisConstant.KeyEnum.ORDER_TOKEN_PREFIX.getKey()+":"+
                AuthorizationInterceptor.userIdThreadLocal.get();
        String orderToken = submitVo.getOrderToken();
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class), Arrays.asList(key), orderToken);

        if(result==0L)
        {
            //验证失败
            responseVo.setCode(OrderResponseVo.ResponseCode.TOKEN_VALIDATION_FAIL.getCode());
            return responseVo;
        }else{
            //验证成功
            //创建订单
            OrderCreateTo order = createOrder(submitVo);
            //验价
            BigDecimal orderPay = order.getOrder().getTotalAmount();
            BigDecimal confirmPay = submitVo.getPayPrice();
            if(Math.abs(orderPay.subtract(confirmPay).doubleValue())<0.01)
            {
                //对比成功
                responseVo.setCode(0);
                //保存订单和订单项到数据库
                saveOrder(order);
                //库存锁定
                R lockR = wmsFeignService.lockSkuStock(order.getItemLocks());
                if (lockR.getCode()!=0)
                {
                    //锁库存失败
                    responseVo.setCode(OrderResponseVo.ResponseCode.LOCK_STOCK_FAIL.getCode());
                    return responseVo;
                }
                responseVo.setOrder(order.getOrder());
                //发送消息到延时队列
                rabbitTemplate.convertAndSend(MyMQConfig.ORDER_EVENT_EXCHANGE,
                        MyMQConfig.EXCHANGE_DELAY_ROUTING_KEY,order.getOrder());
                return responseVo;
            }else {
                //验价失败
                responseVo.setCode(OrderResponseVo.ResponseCode.PRICE_VALIDATION_FAIL.getCode());
            }
            return responseVo;
        }
    }


    /***
     * 保存订单到数据库
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
         OrderEntity orderEntity = order.getOrder();
         orderEntity.setModifyTime(new Date());
        this.baseMapper.insert(orderEntity);
        for (OrderItemEntity orderItem:order.getOrderItems())
            orderItem.setOrderId(orderEntity.getId());
        orderItemService.saveBatch(order.getOrderItems());
    }

    private OrderCreateTo createOrder(OrderSubmitVo submitVo)
    {
        OrderCreateTo createTo = new OrderCreateTo();
        //生成一个订单号
        String orderSn = IdWorker.getTimeId();
        //创建订单
        OrderEntity order = buildOrder(submitVo, orderSn);


        //获取到所有的订单项信息
        List<LockStockVo> lockInfos = new ArrayList<>();
        List<OrderItemEntity> items = buildOrderItems(orderSn,lockInfos);

        //验价
        computePrice(order,items);


        createTo.setOrder(order);
        createTo.setOrderItems(items);
        createTo.setItemLocks(lockInfos);
        return createTo;
    }


    private void computePrice(OrderEntity order, List<OrderItemEntity> items) {
        //订单价格相关
        BigDecimal totalPrice = new BigDecimal("0");
        //优惠总金额
        BigDecimal couponTotal = new BigDecimal("0");
        BigDecimal intergrationTotal = new BigDecimal("0");
        BigDecimal promotionTotal = new BigDecimal("0");
        //成长值
        Integer gift = Integer.valueOf(0);
        Integer growth = Integer.valueOf(0);
        for(OrderItemEntity item:items)
        {
            BigDecimal realAmount = item.getRealAmount();
            totalPrice = totalPrice.add(realAmount);
            //优惠总金额
            couponTotal.add(item.getCouponAmount());
            intergrationTotal.add(item.getIntegrationAmount());
            promotionTotal.add(item.getPromotionAmount());
            //获得积分成长值
            gift+=item.getGiftIntegration();
            growth+=item.getGiftGrowth();
        }
        order.setTotalAmount(totalPrice);
        order.setPayAmount(totalPrice.add(order.getFreightAmount()));
        order.setCouponAmount(couponTotal);
        order.setIntegrationAmount(intergrationTotal);
        order.setPromotionAmount(promotionTotal);
        order.setIntegration(gift);
        order.setGrowth(growth);
    }

    /***
     * 构建订单
     * @param submitVo
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitVo submitVo, String orderSn) {
        OrderEntity order = new OrderEntity();
        order.setOrderSn(orderSn);
        order.setMemberId(AuthorizationInterceptor.userIdThreadLocal.get());
        //获取收货地址信息
        R addressFare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareVo = addressFare.getData(new TypeReference<FareVo>() {
        });
        if(fareVo!=null)
        {
            order.setFreightAmount(fareVo.getFare());
            order.setReceiverProvince(fareVo.getMemberAddressVo().getProvince());
            order.setReceiverRegion(fareVo.getMemberAddressVo().getRegion());
            order.setReceiverCity(fareVo.getMemberAddressVo().getCity());
            order.setReceiverDetailAddress(fareVo.getMemberAddressVo().getDetailAddress());
            order.setReceiverName(fareVo.getMemberAddressVo().getName());
            order.setReceiverPhone(fareVo.getMemberAddressVo().getPhone());
            order.setReceiverPostCode(fareVo.getMemberAddressVo().getPostCode());
        }
        //设置订单状态信息
        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        order.setAutoConfirmDay(7);
        return order;
    }

    /***
     * 构建订单项数据
     * @param orderSn 订单号
     * @param lockInfos
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn, List<LockStockVo> lockInfos) {
        List<OrderItemVo> orderItems;
        try{
            orderItems = cartFeignService.getCurrentUserCartItems();
        }catch (Exception e)
        {
            return null;
        }
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        if(orderItems!=null)
        {
            for (OrderItemVo item:orderItems)
            {
                OrderItemEntity orderItem = buildOrderItem(item);
                orderItem.setOrderSn(orderSn);
                orderItemEntities.add(orderItem);
                //锁库存信息
                LockStockVo lockInfo = new LockStockVo();
                lockInfo.setOrderSn(orderSn);
                lockInfo.setSkuId(item.getSkuId());
                lockInfo.setLockCount(item.getCount());
                lockInfos.add(lockInfo);
            }
        }
        return orderItemEntities;
    }

    /**
     * 构建某一个订单项
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity orderItem = new OrderItemEntity();
        //商品的spu信息
        SpuInfoVo spuInfoVo = null;
        try {
            R spuInfoR = productFeignService.getInfoBySkuId(item.getSkuId());
            spuInfoVo = spuInfoR.getData(new TypeReference<SpuInfoVo>(){});
            if(spuInfoVo!=null)
            {
                orderItem.setSpuId(spuInfoVo.getId());
                String brandName = productFeignService.brandName(spuInfoVo.getBrandId());
                orderItem.setSpuBrand(brandName);
                orderItem.setSpuPic(spuInfoVo.getSpuDescription());
                orderItem.setSpuName(spuInfoVo.getSpuName());
                orderItem.setCategoryId(spuInfoVo.getCatalogId());
            }
        }catch (Exception e)
        {
            //远程调用可能出现的异常
        }
        //商品的sku信息
        orderItem.setSkuId(item.getSkuId());
        orderItem.setSkuName(item.getTitle());
        orderItem.setSkuPic(item.getImage());
        orderItem.setSkuPrice(item.getPrice());
        orderItem.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(),";"));
        orderItem.setSkuQuantity(item.getCount());
        //积分信息
        orderItem.setGiftGrowth(item.getPrice().intValue());
        orderItem.setGiftIntegration(item.getPrice().intValue());

        //订单项价格信息
        //促销优惠
        orderItem.setPromotionAmount(new BigDecimal("0"));
        //优惠券优惠
        orderItem.setCouponAmount(new BigDecimal("0"));
        //积分优惠
        orderItem.setIntegrationAmount(new BigDecimal("0"));
        //实际金额
        BigDecimal origin = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity()));
        BigDecimal real = origin.subtract(orderItem.getPromotionAmount()).subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getIntegrationAmount());
        orderItem.setRealAmount(real);
        return orderItem;
    }

}