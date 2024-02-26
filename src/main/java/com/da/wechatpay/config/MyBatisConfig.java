package com.da.wechatpay.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * mybatis分页插件配置
 * Configuration 作用是开启注解的支持
 */
@Configuration
public class MyBatisConfig {
    @Bean
    public PageHelper pageHelper(){// 配置分页插件
        PageHelper pageHelper = new PageHelper();// 设置数据库类型 Oracle,Mysql,MariaDB,SQLite,Hsqldb,PostgreSQL六种数据库
        // Properties 作用是设置分页插件的属性
        Properties p = new Properties();

        // 设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用
        p.setProperty("offsetAsPageNum","true");// offsetAsPageNum=true时，会将RowBounds第一个参数offset当成pageNum页码使用

        //设置为true时，使用RowBounds分页会进行count查询
        p.setProperty("rowBoundsWithCount","true");// rowBoundsWithCount=true时，使用RowBounds分页会进行count查询
        p.setProperty("reasonable","true");// reasonable=true时，pageNum<=0 时会查询第一页， pageNum>pages（超过总数时），会查询最后一页
        pageHelper.setProperties(p);// 将上面设置的属性值进行赋值
        return pageHelper;// 返回pageHelper对象
    }
}