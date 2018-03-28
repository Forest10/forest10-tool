package forest10.encrypt;

import java.util.*;

public class SignUtil {

    public static final String[] DEFAULT_EXCLUDE_SIGN_KEY = {"sign", "sign_type", "key"};

    /**
     * 过滤掉空值和需要排除的key
     *
     * @param params
     * @return
     */
    private static Map<String, Object> grepEmptyValueAndExcludeKey(Map<String, Object> params) {
        //List<String> excludeKeys = Stream.of(DEFAULT_EXCLUDE_SIGN_KEY).collect(Collectors.toList());
        List<String> excludeKeys = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        params.forEach((K, V) -> {
            if (Objects.nonNull(V) && !Objects.equals(V, "") && !excludeKeys.contains(K)) {
                map.put(K, V);
            }
        });
        return map;
    }

    /**
     * 按字典序拼接参数, [参数名=参数值, ...]; 字典序 key 区分大小写;
     *
     * @param params
     *            参数及参数值
     * @param //excludeKeys
     *            不参与拼接的key，有默认值
     */
    private static LinkedList<String> spliceSortedParamsList(Map<String, Object> params) {
        Map<String, Object> signPairs = grepEmptyValueAndExcludeKey(params);
        LinkedList<String> linkedList = new LinkedList<>();
        signPairs.entrySet().stream().sorted(Map.Entry.<String, Object>comparingByKey())
                .forEachOrdered(x -> linkedList.add(x.getKey() + "=" + x.getValue()));
        return linkedList;

    }

    /**
     * 签名
     * <p>
     * 参数名ASCII码从小到大排序（字典序）； 如果参数的值为空不参与签名； 参数名区分大小写； secret
     * 参与排序，参与签名；
     *
     * @param params
     * @param secret 分配给服务的对称密钥
     * @return
     */
    private static String signParams(Map<String, Object> params, String secret) {
        if (Objects.isNull(params) || Objects.isNull(secret)) {
            return null;
        }
        params.put("secret", secret);
        LinkedList<String> signPairs = spliceSortedParamsList(params);
        StringBuffer signBeforeMd5 = new StringBuffer();
        signPairs.forEach(str -> signBeforeMd5.append(str).append("&"));
        signBeforeMd5.deleteCharAt(signBeforeMd5.lastIndexOf("&"));

        return MD5Util.MD5Encode(signBeforeMd5.toString()).toUpperCase();

    }
    /**
     * 签名参数，返回完整的请求参数
     * <p>
     * 参数的值为空不参与签名，因此请求的参数列表中也去掉参数值为空的参数
     *
     * @param params
     * @param secret
     * @param appId
     * @return 1497084197235
     */
    static Map<String, Object> integrateParams(Map<String, Object> params, String secret, String appId) {
        if (params == null) {
            params = new HashMap<>();
        }
        Map<String, Object> appParams = new HashMap<>();
        appParams.putAll(params);
        appParams.put("appid", appId);
        appParams.put("timestamp", System.currentTimeMillis());
        appParams.put("requestId", UUID.randomUUID());
        String sign = signParams(appParams, secret);

        appParams.put("sign", sign);
        return grepEmptyValueAndExcludeKey(appParams);

    }


}


