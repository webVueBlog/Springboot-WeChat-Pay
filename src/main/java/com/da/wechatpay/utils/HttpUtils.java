package com.da.wechatpay.utils;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装http get post
 */
public class HttpUtils {

    private static  final Gson gson = new Gson();//json转换工具

    /**
     * get方法
     * @param url
     * @return
     */
    public static Map<String,Object> doGet(String url){

        Map<String,Object> map = new HashMap<>();
        CloseableHttpClient httpClient =  HttpClients.createDefault();

        RequestConfig requestConfig =  RequestConfig.custom().setConnectTimeout(5000) //连接超时
                .setConnectionRequestTimeout(5000)//请求超时
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true)  //允许自动重定向
                .build();

        HttpGet httpGet = new HttpGet(url);//创建get请求
        httpGet.setConfig(requestConfig);

        try{
           HttpResponse httpResponse = httpClient.execute(httpGet);
           if(httpResponse.getStatusLine().getStatusCode() == 200){

//              String jsonResult = EntityUtils.toString( httpResponse.getEntity());
              String jsonResult = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

               map = gson.fromJson(jsonResult,map.getClass());
           }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }


    /**
     * 封装post
     * @return
     */
    public static String doPost(String url, String data,int timeout){
        CloseableHttpClient httpClient =  HttpClients.createDefault();
        //超时设置

        RequestConfig requestConfig =  RequestConfig.custom().setConnectTimeout(timeout) //连接超时
                .setConnectionRequestTimeout(timeout)//请求超时
                .setSocketTimeout(timeout)//读取超时
                .setRedirectsEnabled(true)  //允许自动重定向
                .build();


        HttpPost httpPost  = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type","text/html; chartset=UTF-8");

        if(data != null && data instanceof  String){ //使用字符串传参
            StringEntity stringEntity = new StringEntity(data,"UTF-8");
            httpPost.setEntity(stringEntity);
        }

        try{

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);//执行请求
            HttpEntity httpEntity = httpResponse.getEntity();//获取返回数据
            if(httpResponse.getStatusLine().getStatusCode() == 200){//请求成功
                String result = EntityUtils.toString(httpEntity,"UTF-8");//将返回数据转换为字符串
                return result;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;

    }


}
