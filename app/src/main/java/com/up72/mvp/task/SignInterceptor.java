package com.up72.mvp.task;

import com.up72.library.utils.StringUtils;
import com.up72.library.utils.security.BASE64Encoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 签名拦截器
 * Created by LYF on 2016/12/28.
 */
class SignInterceptor implements Interceptor {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Map<String, String> fixedMap = new HashMap<>();
        fixedMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        fixedMap.put("secretId", "client_up72");
        fixedMap.put("nonce",String.valueOf(new Random().nextInt(10000) + 1));
        Map<String, String> paramsMap = new HashMap<>();
        Request.Builder requestBuilder = request.newBuilder();
        if (request.body() instanceof FormBody) {
            FormBody.Builder newFormBody = new FormBody.Builder();
            FormBody oldFormBody = (FormBody) request.body();
            for (int i = 0; i < oldFormBody.size(); i++) {
                paramsMap.put(oldFormBody.name(i), oldFormBody.value(i));
            }
            newFormBody.add("body", getBody(fixedMap, paramsMap));
            requestBuilder.method(request.method(), newFormBody.build());
        }
        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }

    private String getBody(Map<String, String> fixed, Map<String, String> body) {
        String sign = getSign(fixed, body);
        JSONObject bodyJson = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : fixed.entrySet()) {
                bodyJson.put(entry.getKey(), entry.getValue());
            }
            JSONObject data = new JSONObject();
            for (Map.Entry<String, String> entry : body.entrySet()) {
                data.put(entry.getKey(), entry.getValue());
            }
            bodyJson.put("sign", sign);
            bodyJson.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getBASE64(bodyJson.toString());
    }

    private String getSign(Map<String, String> fixed, Map<String, String> body) {
        List<String> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : fixed.entrySet()) {
            params.add(entry.getKey() + "=" + entry.getValue());
        }
        for (Map.Entry<String, String> entry : body.entrySet()) {
            params.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] array = params.toArray(new String[params.size()]);
        Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
        String string = StringUtils.join(array, "&");
        string += "&" + "secretKey=Gu5t9xGARNpq86cd98joQYCN3Cozk1qA";
        String result = MD5(string);
        if (result != null) {
            result = getBASE64(result);
        }
        return result;
    }

    /**
     * MD5加密
     */
    private String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes(UTF_8));
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte a : b) {
                i = a;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
           /* System.out.println("MD5(" + sourceStr + ",32) = " + result);
            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));*/
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 将 s 进行 BASE64 编码
    private String getBASE64(String s) {
        if (s == null) {
            return null;
        }
        return (new BASE64Encoder()).encode(s.getBytes(UTF_8));
    }
}