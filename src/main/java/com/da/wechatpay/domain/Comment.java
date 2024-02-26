package com.da.wechatpay.domain;


import lombok.Data;

import java.io.Serializable;

/**
 * 评论实体类
 */
@Data
public class Comment implements Serializable {

  private Integer id;//主键id
  private String content;//评论内容
  private Integer userId;//用户id
  private String headImg;//头像
  private String name;//用户名
  private double point;//评分
  private Integer up;//点赞数
  private java.util.Date createTime;//创建时间
  private Integer orderId;//订单id
  private Integer videoId;//视频id


}
