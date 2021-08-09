package com.example.demoStudy2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demoStudy2.event.Resp.AuthGetTokenResp;
import com.example.demoStudy2.util.EncriptAndDecriptUtils;
import com.example.demoStudy2.util.JCEUtil;
import com.example.demoStudy2.util.SecretUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
@Component
@Slf4j
public class IamSmartInstance {
    @Value("${clientId}")
    private String clientID;
    @Value("${signatureMethod}")
    private String signatureMethod;
    @Value("${url}")
    private String httpUrl;
    @Value("${secret}")
    private String secret;

    @Value("${rsaPrivateKey}")
    private String rsaPrivateKey;

    private String secretKey=null;
    private byte[] aesKey=null;
    private String pubKey=null;
    private long issueAt=-1;
    private long expiresIn=-1;
    private Mac sha256_HMAC;
    private CloseableHttpClient client;
    private static boolean initFlag=false;
    private java.security.interfaces.RSAPrivateKey rSAPrivateKey=null;
    @SneakyThrows
    public void init(){
        if(!initFlag){
            this.sha256_HMAC = Mac.getInstance(signatureMethod);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), signatureMethod);
            this.sha256_HMAC.init(secret_key);
            SSLContext sslContext = null;
            try {
                sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) {
                        return true;
                    }
                }).build();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            //创建httpClient
            this.client = HttpClients.custom().setSSLContext(sslContext).
                    setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            rSAPrivateKey=EncriptAndDecriptUtils.getPrivateKey(rsaPrivateKey);
            //将jdk 针对aes加密超过128位不支持的限制放开
            JCEUtil.removeCryptographyRestrictions();
        }
    }



    @SneakyThrows
    private void setKeyMap() {
        init();
        long timestamp=System.currentTimeMillis();
        UUID u = UUID.randomUUID();
        String nonce =u.toString();
        JSONObject jSONObject=new JSONObject();
        String request_body=jSONObject.toJSONString();
        String message = this.clientID + this.signatureMethod + timestamp+ nonce + request_body;
        String hash = Base64.encodeBase64String(this.sha256_HMAC.doFinal(message.getBytes()));
        String signature = URLEncoder.encode(hash,"UTF-8");
        String url=this.httpUrl+"/api/v1/security/getKey";
        HttpPost httpPost=new HttpPost(url);
        httpPost.setHeader("Content-Type","application/json");
        httpPost.setHeader("clientID",this.clientID);
        httpPost.setHeader("signatureMethod",this.signatureMethod);
        httpPost.setHeader("signature",signature);
        httpPost.setHeader("timestamp",String.valueOf(timestamp));
        httpPost.setHeader("nonce",nonce);
        StringEntity stringEntity = new StringEntity(request_body);
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();
        JSONObject response= JSONObject.parseObject(str);
        JSONObject content= response.getJSONObject("content");
        secretKey=content.getString("secretKey");
        aesKey=EncriptAndDecriptUtils.privateDecrypt1(secretKey,rSAPrivateKey);
        this.pubKey=content.getString("pubKey");
        this.issueAt=content.getLong("issueAt");
        this.expiresIn=content.getLong("expiresIn");
        log.info("secretKey={}",secretKey);
        log.info("pubKey={}",pubKey);
        log.info("issueAt={}",issueAt);
        log.info("expiresIn={}",expiresIn);
    }

    /**获取token*/
    @SneakyThrows
    public AuthGetTokenResp authGetToken(String code){
        init();
        if(-1==issueAt||validate(issueAt,expiresIn)){
            setKeyMap();
        }
        long timestamp=System.currentTimeMillis();
        UUID u = UUID.randomUUID();
        String nonce =u.toString();
        JSONObject jSONObject=new JSONObject();
        jSONObject.put("code",code);
        jSONObject.put("grantType","authorization_code");
        String request_body=jSONObject.toJSONString();
        JSONObject reqDdata=new JSONObject();
        reqDdata.put("content", SecretUtil.encrypt(request_body.getBytes(),aesKey));
        String message = this.clientID + this.signatureMethod + timestamp+ nonce + reqDdata.toString();
        String hash = Base64.encodeBase64String(this.sha256_HMAC.doFinal(message.getBytes()));
        String signature = URLEncoder.encode(hash,"UTF-8");
        String url=this.httpUrl+"/api/v1/auth/getToken";
        HttpPost httpPost=new HttpPost(url);
        httpPost.setHeader("Content-Type","application/json");
        httpPost.setHeader("clientID",this.clientID);
        httpPost.setHeader("signatureMethod",this.signatureMethod);
        httpPost.setHeader("signature",signature);
        httpPost.setHeader("timestamp",String.valueOf(timestamp));
        httpPost.setHeader("nonce",nonce);
        StringEntity stringEntity = new StringEntity(reqDdata.toString());
        log.info(reqDdata.toString());
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();
        log.info(str);
        JSONObject json=JSON.parseObject(str);
        AuthGetTokenResp authGetTokenResp=new AuthGetTokenResp();
        if("D00000".equals(json.getString("code"))){
            String content=json.getString("content");
            log.info(content);
            String result = SecretUtil.decrypt(Base64.decodeBase64(content),aesKey);
            JSONObject contentData=JSON.parseObject(result);
            authGetTokenResp.setAccessToken(contentData.getString("accessToken"));
            authGetTokenResp.setExpiresIn(contentData.getLong("expiresIn"));
            authGetTokenResp.setTokenType(contentData.getString("tokenType"));
            authGetTokenResp.setIssueAt(contentData.getString("issueAt"));
            authGetTokenResp.setExpressed(contentData.getString("expressed"));
            authGetTokenResp.setMilliseconds(contentData.getString("milliseconds"));
            authGetTokenResp.setOpenID(contentData.getString("openID"));
            authGetTokenResp.setLastModifiedDate(contentData.getLong("lastModifiedDate"));
            authGetTokenResp.setUserType(contentData.getString("userType"));
            authGetTokenResp.setScope(contentData.getString("scope"));
            log.info(result);
        }
        return authGetTokenResp;
    }

    /**
     * 校验是否超过一天有效期
     * @param issueAt 生效时间
     * @param expiresIn 有效时间
     * @return true 需要重新获取    false 当前可用
     */
    public boolean validate(long issueAt,long expiresIn){
        long now=System.currentTimeMillis()-86400000;
        if((issueAt+expiresIn)<=now){
            return true;
        }else{
            return false;
        }

    }

}
