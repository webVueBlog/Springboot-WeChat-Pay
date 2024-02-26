package com.da.wechatpay.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

//import static com.sun.org.apache.xml.internal.security.parser.XMLParserImpl.getDocumentBuilder;

/**
 * 微信支付工具类，xml转map,map转xml，生成签名
 */
public class WXPayUtil {

    //getDocumentBuilder作用是
    // 创建一个新的DocumentBuilder对象，用于解析XML文档。
    // DocumentBuilder对象用于将XML文档转换为DOM对象，以便进行进一步的处理和操作。
    // 该方法确保了XML文档的安全性和正确性，并确保了DOM对象的正确性和可用性。
    // 它还提供了一些配置选项，以便在解析XML文档时进行自定义设置。
    // 因此，getDocumentBuilder方法用于创建一个DocumentBuilder对象，用于解析XML文档。
    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        // 创建一个DocumentBuilderFactory对象
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();// 创建一个新的DocumentBuilderFactory对象
        // 设置XML解析器的各种配置选项，以确保安全性和正确性
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);// 禁止文档类型声明
        // 禁止外部实体的引用，以防止外部实体注入攻击
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);// 禁止外部实体注入攻击
        // 禁止外部参数实体的引用，以防止外部参数实体注入攻击
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);// 禁止外部参数实体注入攻击
        // 禁止DTD解析，以防止外部DTD引用攻击
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);// 禁止DTD解析，以防止外部DTD引用攻击
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);// 启用安全模式，以防止XXE攻击
        documentBuilderFactory.setXIncludeAware(false);// 禁止XInclude，以防止XXE攻击
        documentBuilderFactory.setExpandEntityReferences(false);// 禁止实体引用扩展，以防止XXE攻击
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();// 创建一个DocumentBuilder对象，用于解析XML文档
        return documentBuilder;// 返回DocumentBuilder对象
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();// 存储XML数据
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));// 将XML数据转换为输入流
            DocumentBuilder documentBuilder=getDocumentBuilder();// 获取DocumentBuilder对象
            Document doc = documentBuilder.parse(stream);// 解析XML文档
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
//            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();// 获取XML根元素
            NodeList nodeList = doc.getDocumentElement().getChildNodes();// 获取根元素的所有子节点
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {// 遍历所有子节点
                Node node = nodeList.item(idx);// 获取某个子节点对象
                if (node.getNodeType() == Node.ELEMENT_NODE) {// 判断节点类型是否为元素
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;// 将节点对象转换
                    data.put(element.getNodeName(), element.getTextContent());// 将元素标签和元素内容存储到Map中
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }


    /**
     * 生成微信支付sign
     * @return
     */
    public static String createSign(SortedMap<String, String> params, String key){
        StringBuilder sb = new StringBuilder();//拼接字符串
        Set<Map.Entry<String, String>> es =  params.entrySet();//返回此映射中包含的映射关系的 Set 视图。
        Iterator<Map.Entry<String,String>> it =  es.iterator();//返回此集合中元素的一个迭代器

        //生成 stringA="appid=wxd930ea5d5a258f4f&body=test&device_info=1000&mch_id=10000100&nonce_str=ibuaiVcKdpRxkhJA";
        while (it.hasNext()){//判断是否存在下一个元素
            Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();//获取下一个元素
             String k = (String)entry.getKey();//获取key
             String v = (String)entry.getValue();//获取value
             if(null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)){//判断value是否为空
                sb.append(k+"="+v+"&");//拼接字符串
             }
        }

        sb.append("key=").append(key);//拼接key
        String sign = CommonUtils.MD5(sb.toString()).toUpperCase();//MD5加密
        return sign;
    }


    /**
     * 校验签名
     * @param params
     * @param key
     * @return
     */
    public static boolean isCorrectSign(SortedMap<String, String> params, String key){
        String sign = createSign(params,key);//生成签名

        String weixinPaySign = params.get("sign").toUpperCase();//获取微信签名

        return weixinPaySign.equals(sign);//判断签名是否一致
    }


    /**
     * 获取有序map
     * @param map
     * @return
     */
    public static SortedMap<String,String> getSortedMap(Map<String,String> map){

        SortedMap<String, String> sortedMap = new TreeMap<>();//根据key进行排序
        Iterator<String> it =  map.keySet().iterator();//获取key的迭代器
        while (it.hasNext()){//判断是否有下一个元素
            String key  = (String)it.next();//获取下一个元素
            String value = map.get(key);//获取对应的value
            String temp = "";//临时变量
            if( null != value){//判断value是否为空
                temp = value.trim();//去除空格
            }
            sortedMap.put(key,temp);//将key和value放入sortedMap中
        }
        return sortedMap;//返回sortedMap
    }



}
