package com.da.wechatpay.controller;

import com.da.wechatpay.dto.VideoOrderDto;
import com.da.wechatpay.utils.WXPayUtil;
import com.da.wechatpay.config.WeChatConfig;
import com.da.wechatpay.domain.VideoOrder;
import com.da.wechatpay.server.VideoOrderService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private WeChatConfig weChatConfig;

    @Autowired
    private VideoOrderService videoOrderService;

    /**
     * 用户点击购买下单接口
     */
    @GetMapping("buy")
    public void saveOrder(@RequestParam(value = "video_id",required = true)int videoId,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        /**
         * 实际开发需要获取用户id和用户当前ip，这里临时写死的配置
         * String ip = IpUtils.getIpAddr(request);
         * int userId = request.getAttribute("user_id");
         */
        int userId = 1;
        String ip = "192.25.1.43";
        //1、根据用户id和商品id生成订单
        VideoOrderDto videoOrderDto = new VideoOrderDto();//订单dto
        videoOrderDto.setUserId(userId);//用户id
        videoOrderDto.setVideoId(videoId);//商品id
        videoOrderDto.setIp(ip);//用户ip

        //2、保存订单同时返回codeUrl
        String codeUrl = videoOrderService.save(videoOrderDto);//保存订单同时返回codeUrl
        if(codeUrl == null) {//保存失败
            throw new  NullPointerException();//抛出空指针异常
        }

        //3、通过google工具生成二维码供用户扫码支付
         try{
            //3、1生成二维码配置
            Map<EncodeHintType,Object> hints =  new HashMap<>();

            //3、2设置纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);

            //3、3编码类型
            hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl,BarcodeFormat.QR_CODE,400,400,hints);
            OutputStream out =  response.getOutputStream();

            MatrixToImageWriter.writeToStream(bitMatrix,"png",out);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 微信支付回调
     * 该链接是通过【统一下单API】中提交的参数notify_url设置，如果链接无法访问，商户将无法接收到微信通知。
     * notify_url不能有参数，外网可以直接访问，不能有访问控制（比如必须要登录才能操作）。示例：notify_url：“https://pay.weixin.qq.com/wxpay/pay.action”
     * 支付完成后，微信会把相关支付结果和用户信息发送给商户，商户需要接收处理，并返回应答。
     * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功。
     *（通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
     * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
     * 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，如果没有处理过再进行处理，如果处理过直接返回结果成功。在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 特别提醒：商户系统对于支付结果通知的内容一定要做签名验证，防止数据泄漏导致出现“假通知”，造成资金损失。
     */
    @RequestMapping("callback")
    public void orderCallback(HttpServletRequest request,HttpServletResponse response) throws Exception {

        InputStream inputStream =  request.getInputStream();//微信回调信息
        //InputStreamReader 继承自Reader
        //Reader是字符输入流，用于读取单字节字符流
        //InputStreamReader 是字节流通向字符流的桥梁，它使用指定的charset读取字节，并将其解码为字符。

        //BufferedReader是包装设计模式，性能更高
        BufferedReader in =  new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        StringBuffer sb = new StringBuffer();//存储微信回调信息
        //1、将微信回调信息转为字符串
        String line ;
        while ((line = in.readLine()) != null){//读取一行数据
            sb.append(line);//将读取的数据拼接到sb中
        }
        in.close();//关闭流
        inputStream.close();//关闭流

        //2、将xml格式字符串格式转为map集合
        Map<String,String> callbackMap = WXPayUtil.xmlToMap(sb.toString());//将xml格式字符串转为map集合
        System.out.println(callbackMap.toString());//打印微信回调信息

        //3、转为有序的map
        SortedMap<String,String> sortedMap = WXPayUtil.getSortedMap(callbackMap);//转为有序的map

        //4、判断签名是否正确
        if(WXPayUtil.isCorrectSign(sortedMap,weChatConfig.getKey())){//判断签名是否正确

            //5、判断回调信息是否成功
            if("SUCCESS".equals(sortedMap.get("result_code"))){//判断回调信息是否成功

                //获取商户订单号
                //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一
                String outTradeNo = sortedMap.get("out_trade_no");//获取商户订单号
                System.out.println(outTradeNo);//打印订单号
                //6、数据库查找订单,如果存在则根据订单号更新该订单
                VideoOrder dbVideoOrder = videoOrderService.findByOutTradeNo(outTradeNo);
                System.out.println(dbVideoOrder);
                if(dbVideoOrder != null && dbVideoOrder.getState()==0){  //判断逻辑看业务场景
                    VideoOrder videoOrder = new VideoOrder();//创建订单对象
                    videoOrder.setOpenid(sortedMap.get("openid"));//设置微信用户标识
                    videoOrder.setOutTradeNo(outTradeNo);//设置商户订单号
                    videoOrder.setNotifyTime(new Date());//设置回调时间
                    //修改支付状态，之前生成的订单支付状态是未支付，这里表面已经支付成功的订单
                    videoOrder.setState(1);//设置支付状态为已支付
                    //根据商户订单号更新订单
                    int rows = videoOrderService.updateVideoOderByOutTradeNo(videoOrder);//更新订单
                    System.out.println(rows);
                    //7、通知微信订单处理成功
                    if(rows == 1){//判断更新结果
                        response.setContentType("text/xml");//设置响应类型为xml
                        response.getWriter().println("success");//返回微信成功标识
                        return;
                    }
                }
            }
        }
        //7、通知微信订单处理失败
        response.setContentType("text/xml");
        response.getWriter().println("fail");

    }

}
