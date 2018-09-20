package com.forest10.wxpay;

import com.forest10.wxpay.constants.WxPayConstants;
import com.google.common.collect.Maps;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 微信支付所需
 *
 * @author Forest10
 * 2017/12/6
 */
public class WxPayUtil {

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = Maps.newHashMap();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement()
                .normalize();
            NodeList nodeList = doc.getDocumentElement()
                .getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element)node;
                    data.put(element.getNodeName(), element.getTextContent());
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
     * @throws ParserConfigurationException      解析错误
     * @throws TransformerConfigurationException 解析错误
     */
    public static String mapToXml(Map<String, String> data) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key : data.keySet()) {
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
        String output = writer.getBuffer()
            .toString()
            .replaceAll("\n|\r", "");
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }

    /**
     * 生成带有 sign 的 XML 格式字符串
     *
     * @param data Map类型数据
     * @param key  API密钥
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key) throws Exception {
        return generateSignedXml(data, key, WxPayConstants.SignType.MD5);
    }

    /**
     * 生成带有 sign 的 XML 格式字符串
     *
     * @param data     Map类型数据
     * @param key      API密钥
     * @param signType 签名类型
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key, WxPayConstants.SignType signType)
        throws Exception {
        String sign = generateSignature(data, key, signType);
        data.put(WxPayConstants.FIELD_SIGN, sign);
        return mapToXml(data);
    }

    /**
     * 判断签名是否正确
     *
     * @param xmlStr XML格式数据
     * @param key    API密钥
     * @return 签名是否正确
     * @throws Exception 错误
     */
    public static boolean isSignatureValid(String xmlStr, String key) throws Exception {
        Map<String, String> data = xmlToMap(xmlStr);
        if (!data.containsKey(WxPayConstants.FIELD_SIGN)) {
            return false;
        }
        String sign = data.get(WxPayConstants.FIELD_SIGN);
        return generateSignature(data, key).equals(sign);
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。使用MD5签名。
     *
     * @param data Map类型数据
     * @param key  API密钥
     * @return 签名是否正确
     * @throws Exception 错误
     */
    public static boolean isSignatureValid(Map<String, String> data, String key) throws Exception {
        return isSignatureValid(data, key, WxPayConstants.SignType.MD5);
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。
     *
     * @param data     Map类型数据
     * @param key      API密钥
     * @param signType 签名方式
     * @return 签名是否正确
     * @throws Exception 错误
     */
    public static boolean isSignatureValid(Map<String, String> data, String key, WxPayConstants.SignType signType)
        throws Exception {
        if (!data.containsKey(WxPayConstants.FIELD_SIGN)) {
            return false;
        }
        String sign = data.get(WxPayConstants.FIELD_SIGN);
        return generateSignature(data, key, signType).equals(sign);
    }

    /**
     * 生成签名
     *
     * @param data 待签名数据
     * @param key  API密钥
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key) throws Exception {
        return generateSignature(data, key, WxPayConstants.SignType.MD5);
    }

    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data     待签名数据
     * @param key      API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key, WxPayConstants.SignType signType)
        throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(WxPayConstants.FIELD_SIGN)) {
                continue;
            }
            // 参数值为空，则不参与签名
            if (data.get(k)
                .trim()
                .length() > 0) {
                sb.append(k)
                    .append("=")
                    .append(data.get(k)
                        .trim())
                    .append("&");
            }
        }
        sb.append("key=")
            .append(key);
        if (WxPayConstants.SignType.MD5.equals(signType)) {
            return md5(sb.toString()).toUpperCase();
        } else if (WxPayConstants.SignType.HMACSHA256.equals(signType)) {
            return hmacsha256(sb.toString(), key);
        } else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }

    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        return UUID.randomUUID()
            .toString()
            .replaceAll("-", "")
            .substring(0, 32);
    }

    /**
     * 生成 md5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String md5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("md5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100)
                .substring(1, 3));
        }
        return sb.toString()
            .toUpperCase();
    }

    /**
     * 生成 hmacsha256
     *
     * @param data 待处理数据
     * @param key  密钥
     * @return 加密结果
     * @throws Exception 错误
     */
    public static String hmacsha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100)
                .substring(1, 3));
        }
        return sb.toString()
            .toUpperCase();
    }

    /**
     * 获取当前时间戳，单位秒
     *
     * @return 当前时间戳，单位秒
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间戳，单位毫秒
     *
     * @return 当前时间戳，单位毫秒
     */
    public static long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    /**
     * 生成 uuid， 即用来标识一笔单，也用做 nonce_str
     *
     * @return nonce_str
     */
    public static String generateUUID() {
        return UUID.randomUUID()
            .toString()
            .replaceAll("-", "")
            .substring(0, 32);
    }

}
