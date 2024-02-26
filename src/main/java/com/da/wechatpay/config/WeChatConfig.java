package com.da.wechatpay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 微信配置类
 * Configuration 作为配置类，必须添加@Configuration注解，
 * 表明当前类是一个配置类，相当于一个Spring配置文件。
 * PropertySource 注解，表明当前类是一个属性配置类，相当于一个properties文件。
 */
@Configuration
@PropertySource(value="classpath:application.properties")
@Data
public class WeChatConfig {

    /**
     * 公众号appid
     */
    @Value("${wxpay.appid}")
    private String appId;

    /**
     * 公众号秘钥
     */
    @Value("${wxpay.appsecret}")
    private String appsecret;

    /**
     * 商户号id
     */
    @Value("${wxpay.mer_id}")
    private String mchId;

    /**
     * 支付key
     */
    @Value("${wxpay.key}")
    private String key;

    /**
     * 微信支付回调url
     */
    @Value("${wxpay.callback}")
    private String payCallbackUrl;


    /**
     * 统一下单url
     * https://api.mch.weixin.qq.com/pay/unifiedorder
     * UNIFIED_ORDER_URL 是一个常量，用于存储统一下单接口的URL地址。
     * 这样做可以避免在每个需要使用该URL的地方都重复写一遍相同的字符串，提高代码的可读性和维护性。
     * 同时，使用常量还可以避免在代码中出现拼写错误或者URL地址修改不及时的问题。
     * 这样可以确保在代码中使用的URL地址始终是正确的。
     */
    private static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    public static String getUnifiedOrderUrl() {
        return UNIFIED_ORDER_URL;
    }


}
