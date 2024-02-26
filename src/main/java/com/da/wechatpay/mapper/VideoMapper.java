package com.da.wechatpay.mapper;

import com.da.wechatpay.provider.VideoProvider;
import com.da.wechatpay.domain.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * video数据访问层
 */
@Mapper
public interface VideoMapper {


    @Select("select * from video")
//    @Results({
//            @Result(column = "cover_img",property = "coverImg"),
//            @Result(column = "create_time",property = "createTime")
//    })
    List<Video> findAll();



    @Select("SELECT * FROM video WHERE id = #{id}")
    Video findById(int id);


    //@Update("UPDATE video SET title=#{title} WHERE id =#{id}")
    // UpdateProvider注解的作用 就是提供动态sql语句
    @UpdateProvider(type = VideoProvider.class,method = "updateVideo")
    int update(Video Video);



    @Delete("DELETE FROM video WHERE id =#{id}")
    int delete(int id);


    @Insert("INSERT INTO `video` ( `title`, `summary`, " +
            "`cover_img`, `view_num`, `price`, `create_time`," +
            " `online`, `point`)" +
            "VALUES" +
            "(#{title}, #{summary}, #{coverImg}, #{viewNum}, #{price},#{createTime}" +
            ",#{online},#{point});")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int save(Video video);

}
