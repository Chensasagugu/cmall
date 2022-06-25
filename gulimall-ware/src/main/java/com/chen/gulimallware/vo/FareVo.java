package com.chen.gulimallware.vo;

import com.chen.gulimallware.feign.MemberFeignService;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chen
 * @date 2022.06.24 14:30
 */
@Data
public class FareVo {

    //收货地址
    private MemberAddressVo memberAddressVo;

    //运费
    private BigDecimal fare;
}
