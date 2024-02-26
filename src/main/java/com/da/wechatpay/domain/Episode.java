package com.da.wechatpay.domain;


import lombok.Data;

import java.io.Serializable;

/**
 * 集实体类
 */
@Data
public class Episode  implements Serializable {

  private Integer id;// 集ID
  private String title;// 集标题
  private Integer num;// 集数
  private String duration;// 集时长
  private String coverImg;// 封面图片
  private Integer videoId;// 视频ID
  private String summary;// 集简介
  private java.util.Date createTime;// 创建时间
  private Integer chapterId;// 章节ID

}
