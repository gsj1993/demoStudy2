package com.example.demoStudy2.iamSmartUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demoStudy2.event.exception.BusinessException;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.crypto.Mac;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * iamSmart 工具类
 */
public class IamSmartUtil {
    /**
     * 请求由参数处理
     * @param clientID
     * @param signatureMethod
     * @param httpUrl
     * @param request_body
     * @param sha256_HMAC
     * @return
     */
    @SneakyThrows
    public static HttpPost initRequest(String clientID, String signatureMethod, String httpUrl, String request_body, Mac sha256_HMAC){
        long timestamp=System.currentTimeMillis();
        UUID u = UUID.randomUUID();
        String nonce =u.toString();
        HttpPost httpPost=new HttpPost(httpUrl);
        httpPost.setHeader("Content-Type","application/json");
        httpPost.setHeader("clientID",clientID);
        httpPost.setHeader("signatureMethod",signatureMethod);
        httpPost.setHeader("timestamp",String.valueOf(timestamp));
        httpPost.setHeader("nonce",nonce);
        String message = clientID + signatureMethod + timestamp+ nonce + request_body;
        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
        String signature = URLEncoder.encode(hash,"UTF-8");
        httpPost.setHeader("signature",signature);
        StringEntity stringEntity = new StringEntity(request_body);
        httpPost.setEntity(stringEntity);
        return httpPost;
    }


    public static JSONObject initResponse(String respData){
        JSONObject json= JSON.parseObject(respData);
        if("D00000".equals(json.getString("code"))){
            return json;
        }
        else{
            throw new BusinessException(json.getString("code"),json.getString("message"));
        }

    }

    /**
     * 校验是否超过一天有效期
     * @param issueAt 生效时间
     * @param expiresIn 有效时间 86400000 一天
     * @return true 需要重新获取    false 当前可用
     */
    public static boolean validate(long issueAt,long expiresIn){
        long now=System.currentTimeMillis()-82800000;//为了防止极限情况，时间改为23小时重新获取
        if((issueAt+expiresIn)<=now){
            return true;
        }else{
            return false;
        }

    }

}
