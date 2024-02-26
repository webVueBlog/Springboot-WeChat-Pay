package com.da.wechatpay.server.impl;

import com.da.wechatpay.dto.VideoOrderDto;
import com.da.wechatpay.mapper.UserMapper;
import com.da.wechatpay.mapper.VideoMapper;
import com.da.wechatpay.mapper.VideoOrderMapper;
import com.da.wechatpay.server.VideoOrderService;
import com.da.wechatpay.utils.CommonUtils;
import com.da.wechatpay.utils.HttpUtils;
import com.da.wechatpay.utils.WXPayUtil;
import com.da.wechatpay.config.WeChatConfig;
import com.da.wechatpay.domain.User;
import com.da.wechatpay.domain.Video;
import com.da.wechatpay.domain.VideoOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
@PropertySource(value="classpath:application.properties")
@Service
public class VideoOrderServiceImpl implements VideoOrderService {


    @Autowired
    private WeChatConfig weChatConfig;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoOrderMapper videoOrderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 公众号appid
     */
    @Value("${wxpay.openId}")
    private String openId;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String save(VideoOrderDto videoOrderDto) throws Exception {

        //1、查找商品信息（这里商品指的是视频课程）
        Video video =  videoMapper.findById(videoOrderDto.getVideoId());

        //2、查找用户信息
        User user = userMapper.findByid(videoOrderDto.getUserId());

        //3、生成订单，插入数据库
        VideoOrder videoOrder = new VideoOrder();
        videoOrder.setTotalFee(video.getPrice());
        videoOrder.setVideoImg(video.getCoverImg());
        videoOrder.setVideoTitle(video.getTitle());
        videoOrder.setCreateTime(new Date());
        videoOrder.setVideoId(video.getId());
        videoOrder.setState(0);
        videoOrder.setUserId(user.getId());
        videoOrder.setHeadImg(user.getHeadImg());
        videoOrder.setNickname(user.getName());
        videoOrder.setDel(0);
        videoOrder.setIp(videoOrderDto.getIp());
        videoOrder.setOutTradeNo(CommonUtils.generateUUID());
        videoOrder.setOpenid(openId);

        videoOrderMapper.insert(videoOrder);//插入数据库

        //4、获取codeurl 统一下单方法
        String codeUrl = unifiedOrder(videoOrder);

        return codeUrl;
    }


    /**
     * 统一下单方法
     * @return
     */
    private String unifiedOrder(VideoOrder videoOrder) throws Exception {


        //4.1、生成签名 按照开发文档需要按字典排序，所以用SortedMap
        SortedMap<String,String> params = new TreeMap<>();
        params.put("appid",weChatConfig.getAppId());         //公众账号ID
        params.put("mch_id", weChatConfig.getMchId());       //商户号
        params.put("nonce_str", CommonUtils.generateUUID()); //随机字符串
        params.put("body",videoOrder.getVideoTitle());       // 商品描述
        params.put("out_trade_no",videoOrder.getOutTradeNo());//商户订单号,商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一
        params.put("total_fee",videoOrder.getTotalFee().toString());//标价金额	分
        params.put("spbill_create_ip",videoOrder.getIp());
        params.put("notify_url",weChatConfig.getPayCallbackUrl());  //通知地址
        params.put("trade_type","NATIVE"); //交易类型 JSAPI 公众号支付 NATIVE 扫码支付 APP APP支付

        //4.2、sign签名 具体规则:https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_3
        String sign = WXPayUtil.createSign(params, weChatConfig.getKey());//签名
        params.put("sign",sign);//签名

        //4.3、map转xml （ WXPayUtil工具类）
        String payXml = WXPayUtil.mapToXml(params);//将map转成xml

        //4.4、回调微信的统一下单接口(HttpUtil工具类）
        String orderStr = HttpUtils.doPost(WeChatConfig.getUnifiedOrderUrl(),payXml,4000);//4000超时时间
        if(null == orderStr) {
            return null;
        }
        //4.5、xml转map （WXPayUtil工具类）
        Map<String, String> unifiedOrderMap =  WXPayUtil.xmlToMap(orderStr);//返回的map
        System.out.println(unifiedOrderMap.toString());

        //4.6、获取最终code_url
        if(unifiedOrderMap != null) {//判断是否成功
            return unifiedOrderMap.get("code_url");//获取二维码连接
        }

        return null;
    }

    @Override
    public VideoOrder findByOutTradeNo(String outTradeNo) {

        return videoOrderMapper.findByOutTradeNo(outTradeNo);//根据商户订单号查询订单
    }

    @Override
    public int updateVideoOderByOutTradeNo(VideoOrder videoOrder) {

        return videoOrderMapper.updateVideoOderByOutTradeNo(videoOrder);//更新订单状态
    }





}
