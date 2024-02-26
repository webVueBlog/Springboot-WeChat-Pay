package com.da.wechatpay.domain;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单表
 */
@Data
public class VideoOrder implements Serializable {

  private Integer id;
  private String openid;

  private String outTradeNo;
  /**
   * 0表示未支付，1表示已经支付
   */
  private Integer state;
  private java.util.Date createTime;
  // notifyTime 注解是用于指定mybatis-plus在查询数据库时，将数据库中datetime类型转换为java.util.Date类型
  //  @org.apache.ibatis.annotations.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private java.util.Date notifyTime;
  /**
   *分为单位
   */
  private Integer totalFee;
  private String nickname;
  private String headImg;
  private Integer videoId;
  private String videoTitle;
  private String videoImg;
  private Integer userId;
  private String ip;
  private Integer del;


}
