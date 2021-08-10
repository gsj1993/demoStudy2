package com.example.demoStudy2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demoStudy2.event.Resp.*;
import com.example.demoStudy2.iamSmartUtil.IamSmartUtil;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;

@Component
@Slf4j
public class IamSmartInstance {
    /**
     * 客户ID  由iam-smart下发
     */
    @Value("${clientId}")
    private String clientID;

    /**
     * 请求头签名参数 默认 HmacSHA256
     */
    @Value("${signatureMethod}")
    private String signatureMethod;
    /**
     * 服务端iam-smart域名
     */
    @Value("${url}")
    private String httpUrl;

    /**
     * 请求与签名加密参数 由iam-smart下发
     */
    @Value("${secret}")
    private String secret;

    /**
     * 获取报文通讯密钥   iam-smart配置公钥的私钥
     */
    @Value("${rsaPrivateKey}")
    private String rsaPrivateKey;

    @Value("${businessID}")
    private String businessID;

    /**
     * 后续报文通讯的密钥 aesKey
     */
    private byte[] aesKey=null;
    /**
     * 报文通讯加密密钥的生效日期  时间戳格式
     */
    private long issueAt=-1;
    /**
     * 报文通讯的加密密钥的有效时间  一天
     */
    private long expiresIn=-1;
    /**
     * 加密方法
     */
    private Mac sha256_HMAC;
    /**
     * http请求
     */
    private CloseableHttpClient client;
    /**
     * 初始话标志
     */
    private static boolean initFlag=false;
    /**
     * rsa密钥
     */
    private java.security.interfaces.RSAPrivateKey rSAPrivateKey=null;

    /**
     * 初始化参数与对象，
     */
    @SneakyThrows
    public void init(){
        if(!initFlag){
            /**
             * 请求头参数签名对象初始化
             */
            this.sha256_HMAC = Mac.getInstance(signatureMethod);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), signatureMethod);
            this.sha256_HMAC.init(secret_key);

            /**
             * httpClient 对象初始化，去除证书验证
             */
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
            this.client = HttpClients.custom().setSSLContext(sslContext).
                    setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            /**
             * ras私钥初始化
             */
            rSAPrivateKey=EncriptAndDecriptUtils.getPrivateKey(rsaPrivateKey);

            /**
             * 将jdk 针对aes加密超过128位不支持的限制放开
             */
            JCEUtil.removeCryptographyRestrictions();
        }
    }


    /**
     * 报文请求获取aes加密密钥
     */
    @SneakyThrows
    private void setKeyMap() {
        //初始化
        init();
        //
        String url=this.httpUrl+"/api/v1/security/getKey";
        JSONObject jSONObject=new JSONObject();
        String request_body=jSONObject.toJSONString();
        HttpPost httpPost= IamSmartUtil.initRequest(clientID,signatureMethod,url,request_body,sha256_HMAC);
        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();
        JSONObject response=IamSmartUtil.initResponse(str);
        JSONObject content= response.getJSONObject("content");
        String secretKey=content.getString("secretKey");
        aesKey=EncriptAndDecriptUtils.privateDecrypt1(secretKey,rSAPrivateKey);
        String pubKey=content.getString("pubKey");
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
        if(-1==issueAt||IamSmartUtil.validate(issueAt,expiresIn)){
            setKeyMap();
        }
        String url=this.httpUrl+"/api/v1/auth/getToken";

        JSONObject jSONObject=new JSONObject();
        jSONObject.put("code",code);
        jSONObject.put("grantType","authorization_code");
        String request_body=jSONObject.toJSONString();
        JSONObject reqDdata=new JSONObject();
        reqDdata.put("content", SecretUtil.encrypt(request_body.getBytes(),aesKey));//请求参数加密

        HttpPost httpPost= IamSmartUtil.initRequest(clientID,signatureMethod,url,reqDdata.toString(),sha256_HMAC);

        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();

        JSONObject response=IamSmartUtil.initResponse(str);
        String content=response.getString("content");
        String result = SecretUtil.decrypt(Base64.decodeBase64(content),aesKey);
        JSONObject contentData=JSON.parseObject(result);
        //返回参数格式处理
        AuthGetTokenResp authGetTokenResp=new AuthGetTokenResp();
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
        return authGetTokenResp;
    }


    @SneakyThrows
    public InitiateResp initiateRequest (String accessToken, String openID, String source, String redirectURI, List<String> profileFields){
        init();
        if(-1==issueAt||IamSmartUtil.validate(issueAt,expiresIn)){
            setKeyMap();
        }
        String url=this.httpUrl+"/api/v1/account/auth/profile/initiateRequest";

        JSONObject jSONObject=new JSONObject();
        jSONObject.put("businessID",businessID);
        jSONObject.put("accessToken",accessToken);
        jSONObject.put("openID",openID);
        jSONObject.put("source",source);
        jSONObject.put("redirectURI",redirectURI);
        jSONObject.put("state",businessID);
        jSONObject.put("profileFields",profileFields);
        String request_body=jSONObject.toJSONString();
        JSONObject reqDdata=new JSONObject();
        reqDdata.put("content", SecretUtil.encrypt(request_body.getBytes(),aesKey));//请求参数加密

        HttpPost httpPost= IamSmartUtil.initRequest(clientID,signatureMethod,url,reqDdata.toString(),sha256_HMAC);

        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();

        JSONObject response=IamSmartUtil.initResponse(str);
        String content=response.getString("content");
        String result = SecretUtil.decrypt(Base64.decodeBase64(content),aesKey);
        JSONObject contentData=JSON.parseObject(result);
        //返回参数格式处理
        InitiateResp initiateResp=new InitiateResp();
        initiateResp.setAuthByQR(contentData.getBoolean("authByQR"));
        initiateResp.setTicketID(contentData.getString("ticketID"));
        return initiateResp;
    }


    @SneakyThrows
    public InitiateEmailResp initiateEmailRequest (String accessToken,String openID,String source,String redirectURI,String formName,String formNum,String formDesc,List<String> eMEFields){
        init();
        if(-1==issueAt||IamSmartUtil.validate(issueAt,expiresIn)){
            setKeyMap();
        }
        String url=this.httpUrl+"/api/v1/account/auth/eme/initiateRequest";

        JSONObject jSONObject=new JSONObject();
        jSONObject.put("businessID",businessID);
        jSONObject.put("accessToken",accessToken);
        jSONObject.put("openID",openID);
        jSONObject.put("source",source);
        jSONObject.put("redirectURI",redirectURI);
        jSONObject.put("state",businessID);
        jSONObject.put("formName",formName);
        jSONObject.put("formNum",formNum);
        jSONObject.put("formDesc",formDesc);
        jSONObject.put("eMEFields",eMEFields);
        String request_body=jSONObject.toJSONString();
        JSONObject reqDdata=new JSONObject();
        reqDdata.put("content", SecretUtil.encrypt(request_body.getBytes(),aesKey));//请求参数加密

        HttpPost httpPost= IamSmartUtil.initRequest(clientID,signatureMethod,url,reqDdata.toString(),sha256_HMAC);

        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();

        JSONObject response=IamSmartUtil.initResponse(str);
        String content=response.getString("content");
        String result = SecretUtil.decrypt(Base64.decodeBase64(content),aesKey);
        JSONObject contentData=JSON.parseObject(result);
        //返回参数格式处理
        InitiateEmailResp initiateEmailResp=new InitiateEmailResp();
        initiateEmailResp.setAuthByQR(contentData.getBoolean("authByQR"));
        initiateEmailResp.setTicketID(contentData.getString("ticketID"));
        return initiateEmailResp;
    }

    @SneakyThrows
    public InitiateSignResp initiateSignRequest (String accessToken, String openID, String source, String redirectURI, String hashCode, String sigAlgo, String HKICHash, String department, String serviceName, String documentName ){
        init();
        if(-1==issueAt||IamSmartUtil.validate(issueAt,expiresIn)){
            setKeyMap();
        }
        String url=this.httpUrl+"/api/v1/account/auth/eme/initiateRequest";

        JSONObject jSONObject=new JSONObject();
        jSONObject.put("businessID",businessID);
        jSONObject.put("accessToken",accessToken);
        jSONObject.put("openID",openID);
        jSONObject.put("source",source);
        jSONObject.put("redirectURI",redirectURI);
        jSONObject.put("state",businessID);
        jSONObject.put("hashCode",hashCode);
        jSONObject.put("sigAlgo",sigAlgo);
        jSONObject.put("HKICHash",HKICHash);
        jSONObject.put("department",department);
        jSONObject.put("serviceName",serviceName);
        jSONObject.put("documentName",documentName);
        String request_body=jSONObject.toJSONString();
        JSONObject reqDdata=new JSONObject();
        reqDdata.put("content", SecretUtil.encrypt(request_body.getBytes(),aesKey));//请求参数加密

        HttpPost httpPost= IamSmartUtil.initRequest(clientID,signatureMethod,url,reqDdata.toString(),sha256_HMAC);

        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();

        JSONObject response=IamSmartUtil.initResponse(str);
        String content=response.getString("content");
        String result = SecretUtil.decrypt(Base64.decodeBase64(content),aesKey);
        JSONObject contentData=JSON.parseObject(result);
        //返回参数格式处理
        InitiateSignResp initiateSignResp=new InitiateSignResp();
        initiateSignResp.setAuthByQR(contentData.getBoolean("authByQR"));
        initiateSignResp.setTicketID(contentData.getString("ticketID"));
        return initiateSignResp;
    }

    @SneakyThrows
    public InitiatePdfSignResp initiatePdfSignRequest (String accessToken, String openID, String source, String redirectURI, String docDigest
    , String HKICHash, String department, String serviceName, String documentName){
        init();
        if(-1==issueAt||IamSmartUtil.validate(issueAt,expiresIn)){
            setKeyMap();
        }
        String url=this.httpUrl+"/api/v1/account/auth/eme/initiateRequest";

        JSONObject jSONObject=new JSONObject();
        jSONObject.put("businessID",businessID);
        jSONObject.put("accessToken",accessToken);
        jSONObject.put("openID",openID);
        jSONObject.put("source",source);
        jSONObject.put("redirectURI",redirectURI);
        jSONObject.put("state",businessID);
        jSONObject.put("docDigest",docDigest);
        jSONObject.put("HKICHash",HKICHash);
        jSONObject.put("department",department);
        jSONObject.put("serviceName",serviceName);
        jSONObject.put("documentName",documentName);
        String request_body=jSONObject.toJSONString();
        JSONObject reqDdata=new JSONObject();
        reqDdata.put("content", SecretUtil.encrypt(request_body.getBytes(),aesKey));//请求参数加密

        HttpPost httpPost= IamSmartUtil.initRequest(clientID,signatureMethod,url,reqDdata.toString(),sha256_HMAC);

        CloseableHttpResponse Response =client.execute(httpPost);
        HttpEntity entity = Response.getEntity();
        // 通过EntityUtils 来将我们的数据转换成字符串
        String str = EntityUtils.toString(entity, "UTF-8");
        Response.close();

        JSONObject response=IamSmartUtil.initResponse(str);
        String content=response.getString("content");
        String result = SecretUtil.decrypt(Base64.decodeBase64(content),aesKey);
        JSONObject contentData=JSON.parseObject(result);
        //返回参数格式处理
        InitiatePdfSignResp initiatePdfSignResp=new InitiatePdfSignResp();
        initiatePdfSignResp.setAuthByQR(contentData.getBoolean("authByQR"));
        initiatePdfSignResp.setTicketID(contentData.getString("ticketID"));
        return initiatePdfSignResp;
    }

}
