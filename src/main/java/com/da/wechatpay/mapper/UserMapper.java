package com.da.wechatpay.mapper;

import com.da.wechatpay.domain.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface UserMapper {

    /**
     * 根据主键id查找
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{id}")
    User findByid(@Param("id") int userId);

    /**
     * 根据openid找用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User findByopenid(@Param("openid") String openid);


    /**
     * 保存用户
     * Options 注解用于设置插入操作的选项
     * useGeneratedKeys 设置为 true，MyBatis 会使用 JDBC 的 getGeneratedKeys 方法来取出由数据库内部生成的键值，并将其设置到键 column 指定的列上。
     * keyProperty 指定能够唯一识别对象的属性，MyBatis 会通过 getGeneratedKeys 的返回值或者通过 insert 语句的
     * SELECT LAST_INSERT_ID() 获取到的主键值，将该值设置到 keyProperty 指定的对象的属性上。
     * keyColumn 用于指定获取到的主键值应被设置到对象的哪个属性上。
     * @param user
     * @return
     */
    @Insert("INSERT INTO `user` ( `openid`, `name`, `head_img`, `phone`, `sign`, `sex`, `city`, `create_time`)" +
            "VALUES" +
            "(#{openid},#{name},#{headImg},#{phone},#{sign},#{sex},#{city},#{createTime});")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    int save(User user);


}
