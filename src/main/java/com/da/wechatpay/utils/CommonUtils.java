package com.da.wechatpay.utils;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * 常用工具类的封装，md5,uuid等
 */
public class CommonUtils {


    /**
     * 生成 uuid， 即用来标识一笔单，也用做 nonce_str
     * substring 方法用于截取字符串，参数为起始位置和结束位置，注意是左闭右开
     * 生成32位uuid
     * @return
     */
    public static String generateUUID(){
        String uuid = UUID.randomUUID().toString().
                replaceAll("-","").substring(0,32);

        return uuid;
    }


    /**
     * md5常用工具类
     * @param data
     * @return
     */
    public static String MD5(String data){
        try {
            // MessageDigest 是Java提供的一种获取信息摘要的类。
            // 信息摘要是一种不可逆的算法，如MD5， SHA1，SHA256等。
            MessageDigest md5 = MessageDigest.getInstance("MD5");// 获取md5加密对象
            // 调用digest() 方法得到程序的加密结果
            // digest() 返回值为存放哈希值结果的byte数组
            byte [] array = md5.digest(data.getBytes("UTF-8"));
            // StringBuilder作用是
            // 用来动态拼接字符串，效率比StringBuffer高，但线程不安全
            // 把数组转换为字符串，并返回
            // 例如：byte[] array = {-21,48,20,-31,103,101,119,-117,116,104,101,32,51,50,45,-118,116,119,-118};
            // 转换为16进制就是：E585B6E289D2E999BFBF
            StringBuilder sb = new StringBuilder();// 创建一个StringBuilder对象
            // 对数组进行遍历
            for (byte item : array) {// 遍历数组
                // Integer.toHexString(int i) 方法用于返回整数参数的字符串表示形式，
                // 该字符串是十六进制数。
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            // 返回拼接后的字符串
            return sb.toString().toUpperCase();// 转换为大写

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }


}
