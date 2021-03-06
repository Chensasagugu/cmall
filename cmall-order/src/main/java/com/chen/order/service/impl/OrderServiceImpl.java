package com.chen.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chen.common.constant.RedisConstant;
import com.chen.common.utils.R;
import com.chen.order.entity.OrderItemEntity;
import com.chen.order.enume.OrderStatusEnum;
import com.chen.order.feign.CartFeignService;
import com.chen.order.feign.MemberFeignService;
import com.chen.order.feign.ProductFeignService;
import com.chen.order.feign.WmsFeignService;
import com.chen.order.interceptor.AuthorizationInterceptor;
import com.chen.order.mq.MyMQConfig;
import com.chen.order.service.OrderItemService;
import com.chen.order.to.OrderCreateTo;
import com.chen.order.vo.*;
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

import com.chen.order.dao.OrderDao;
import com.chen.order.entity.OrderEntity;
import com.chen.order.service.OrderService;
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
        //??????RequestAttributes????????????????????????
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes,true);

        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            //????????????????????????
            List<MemberAddressVo> address = memberFeignService.getAddress(memberId);
            comfirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //?????????????????????????????????
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
                //????????????
                throw new RuntimeException("????????????????????????");
            }
        },executor);

        //??????????????????
        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            Integer integration = memberFeignService.getIntegration(memberId);
            if (integration != null)
                comfirmVo.setIntegration(integration);
        }, executor);

        //??????

        //TODO ????????????
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
        //??????token??????
        //???????????????????????????
        String lua = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String key =  RedisConstant.KeyEnum.ORDER_TOKEN_PREFIX.getKey()+":"+
                AuthorizationInterceptor.userIdThreadLocal.get();
        String orderToken = submitVo.getOrderToken();
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class), Arrays.asList(key), orderToken);

        if(result==0L)
        {
            //????????????
            responseVo.setCode(OrderResponseVo.ResponseCode.TOKEN_VALIDATION_FAIL.getCode());
            return responseVo;
        }else{
            //????????????
            //????????????
            OrderCreateTo order = createOrder(submitVo);
            //??????
            BigDecimal orderPay = order.getOrder().getTotalAmount();
            BigDecimal confirmPay = submitVo.getPayPrice();
            if(Math.abs(orderPay.subtract(confirmPay).doubleValue())<0.01)
            {
                //????????????
                responseVo.setCode(0);
                //????????????????????????????????????
                saveOrder(order);
                //????????????
                R lockR = wmsFeignService.lockSkuStock(order.getItemLocks());
                if (lockR.getCode()!=0)
                {
                    //???????????????
                    responseVo.setCode(OrderResponseVo.ResponseCode.LOCK_STOCK_FAIL.getCode());
                    return responseVo;
                }
                responseVo.setOrder(order.getOrder());
                //???????????????????????????
                rabbitTemplate.convertAndSend(MyMQConfig.ORDER_EVENT_EXCHANGE,
                        MyMQConfig.EXCHANGE_DELAY_ROUTING_KEY,order.getOrder());
                return responseVo;
            }else {
                //????????????
                responseVo.setCode(OrderResponseVo.ResponseCode.PRICE_VALIDATION_FAIL.getCode());
            }
            return responseVo;
        }
    }


    /***
     * ????????????????????????
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
        //?????????????????????
        String orderSn = IdWorker.getTimeId();
        //????????????
        OrderEntity order = buildOrder(submitVo, orderSn);


        //?????????????????????????????????
        List<LockStockVo> lockInfos = new ArrayList<>();
        List<OrderItemEntity> items = buildOrderItems(orderSn,lockInfos);

        //??????
        computePrice(order,items);


        createTo.setOrder(order);
        createTo.setOrderItems(items);
        createTo.setItemLocks(lockInfos);
        return createTo;
    }


    private void computePrice(OrderEntity order, List<OrderItemEntity> items) {
        //??????????????????
        BigDecimal totalPrice = new BigDecimal("0");
        //???????????????
        BigDecimal couponTotal = new BigDecimal("0");
        BigDecimal intergrationTotal = new BigDecimal("0");
        BigDecimal promotionTotal = new BigDecimal("0");
        //?????????
        Integer gift = Integer.valueOf(0);
        Integer growth = Integer.valueOf(0);
        for(OrderItemEntity item:items)
        {
            BigDecimal realAmount = item.getRealAmount();
            totalPrice = totalPrice.add(realAmount);
            //???????????????
            couponTotal.add(item.getCouponAmount());
            intergrationTotal.add(item.getIntegrationAmount());
            promotionTotal.add(item.getPromotionAmount());
            //?????????????????????
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
     * ????????????
     * @param submitVo
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitVo submitVo, String orderSn) {
        OrderEntity order = new OrderEntity();
        order.setOrderSn(orderSn);
        order.setMemberId(AuthorizationInterceptor.userIdThreadLocal.get());
        //????????????????????????
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
        //????????????????????????
        order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        order.setAutoConfirmDay(7);
        return order;
    }

    /***
     * ?????????????????????
     * @param orderSn ?????????
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
                //???????????????
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
     * ????????????????????????
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity orderItem = new OrderItemEntity();
        //?????????spu??????
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
            //?????????????????????????????????
        }
        //?????????sku??????
        orderItem.setSkuId(item.getSkuId());
        orderItem.setSkuName(item.getTitle());
        orderItem.setSkuPic(item.getImage());
        orderItem.setSkuPrice(item.getPrice());
        orderItem.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(),";"));
        orderItem.setSkuQuantity(item.getCount());
        //????????????
        orderItem.setGiftGrowth(item.getPrice().intValue());
        orderItem.setGiftIntegration(item.getPrice().intValue());

        //?????????????????????
        //????????????
        orderItem.setPromotionAmount(new BigDecimal("0"));
        //???????????????
        orderItem.setCouponAmount(new BigDecimal("0"));
        //????????????
        orderItem.setIntegrationAmount(new BigDecimal("0"));
        //????????????
        BigDecimal origin = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity()));
        BigDecimal real = origin.subtract(orderItem.getPromotionAmount()).subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getIntegrationAmount());
        orderItem.setRealAmount(real);
        return orderItem;
    }

}