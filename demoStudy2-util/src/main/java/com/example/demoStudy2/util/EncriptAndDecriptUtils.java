package com.example.demoStudy2.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class EncriptAndDecriptUtils {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    //， ，
    private static final Logger logger = LoggerFactory.getLogger(EncriptAndDecriptUtils.class);

    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;


    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     * @throws
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey)  {
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()));
        }catch(Exception e){//服务繁忙，请稍后再试
            logger.info("解密字符串[" + data + "]时遇到异常");
            throw new IllegalArgumentException("解密字符串[" + data + "]时遇到异常");

        }
    }


    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     * @throws
     */

    public static byte[] privateDecrypt1(String data, RSAPrivateKey privateKey)  {
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return  rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength());
        }catch(Exception e){//服务繁忙，请稍后再试
            logger.info("解密字符串[" + data + "]时遇到异常");
            throw new IllegalArgumentException("解密字符串[" + data + "]时遇到异常");

        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    /**
     * 对字符串进行MD5摘要加密
     *
     * @param input
     * @return 返回值为小写
     */
    public static String md5(String input) {
        if (null == input) {
            input = "";
        }
        String result = "";
        try {
            // MessageDigest类用于为应用程序提供信息摘要算法的功能，如MD5或SHA算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 获取输入
            md.update(input.getBytes());
            // 获得产出（有符号的哈希值字节数组，包含16个元素）
            byte output[] = md.digest();

            // 32位的加密字符串
            StringBuilder builder = new StringBuilder(32);
            // 下面进行十六进制的转换
            for (int offset = 0; offset < output.length; offset++) {
                // 转变成对应的ASSIC值
                int value = output[offset];
                // 将负数转为正数（最终返回结果是无符号的）
                if (value < 0) {
                    value += 256;
                }
                // 小于16，转为十六进制后只有一个字节，左边追加0来补足2个字节
                if (value < 16) {
                    builder.append("0");
                }
                // 将16位byte[]转换为32位无符号String
                builder.append(Integer.toHexString(value));
            }
            result = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 字符串转16进制
     *
     * @param s
     * @return
     */
    public static String string2Hex(String s){
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制转字符串
     *
     * @param hex
     * @return
     */
    public static String hex2String(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }


    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String data="/RYxObw0mtFXEp0IfEm5PuVP+IFycsIbduUMKKHGWfXf1Efe2cmCiIaoUwcNd46ErVJX5u9MsZU5/VvpSOV/64HXxOloe/ouVsbEgAbhJwLySPZuIlmJewQg7BtZvDD2RZGi+GtNgrK6B9B0CVligg7H1SwqoxXHavjEwWNt+epBocAk5C5xbDEp6TZvB8rhsXDocVzeduervIKmz/CLLhFSCIDb34VcDtYMhObzoua+Ssa5rx/b4QRQgrXABafpLMArHyl5dhXw1Xjfk4C/sFPtENE/fP2/+4Q8sKr2hw2IouDA1hnLBd4Cnh0w8QuN+JoGKm32Owb0lI+DmkmZzRg==";
        String rsaPrivateKey="MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQCpFBnZSSkGEHU5RM3qJ80fkwxMG0HwQV5ivgMZygtracqH6IKNN45gFJ3E0VTtwqxuHWY3Euh4tCINE33Uiljcbk0tDGtXrPyUUliwDakb041003iwhTCV+FEqzyXDYbQacnHYcBkW8GPn2AKTWxI8HNrVq3tqQiGkJCcxZaUHDSs8huIqsjkUksea3dNJaWFzwv6ceiYNfrftE0quXZhzDNNuWG3crbU/6sz0UFaQ0sNB4Lc26DsNAMACj6sWuKHJBcGwJX5PRjV8A5hf8dMoyW55MFpit7fFWjk3JQ72oJ22dB0OSTlWNAUWaH1ZTgrrMOtD5m3g7pCHHbWAz6xdAgMBAAECggEBAJoFsqpq6BEbKv45fG9DRKvvs7Mc3Yt0sPu4ZnC2yoJWA+ac21izdGJ4RWSG4RwaTCOfan7fHaDfT2mkCOt2AA8F+A4Ck0JyVkDzZtEyEIxFwkXbYsVC/4iOhbo+rAL5XSz+dlEZhPGNmSUZdPcxbkIOcxdUOIAKsESrnnx0QMpTLK9c4xcNe9RUlhRVoRFmmFTUiOalkjFbuiHXNUqz8C9rCkyrGfAUcNq3eqWnBdRhJyu93kVuEjDA1puXnAJhjNaFEhtbsZCCWHVTvHkxlqGMjjk42P5wrcleNMUDTsCZEycv/dXAGcVWrWfUL1Fl1ogWxDXk1lYBz1marHZ9JMECgYEA0iMtvimywW8d6G+3kqiEGu6h1rAO9b9o0R5o4DVEd8TXbuUmECBSua0/j72FlQsU7a0AuJXMKBN98Jwxc3plgetDbZS4LWwzp1CqWEWc46HFSbVL8dqbyXJsX9S6jXciDDrwLFg/aw/bod52yOtBQD+kbLQJ5a//Ej1jPUa4I9ECgYEAzfrf/j1LN00Er7jGob+CY+ZbZJXn005xGgnOCCGxs2Kx/uOo4oXHquN5ule2chxOsdQPG3PFUxKtZbpcc8fIE05pFDfFk7GX3cCcKoI0c0OLC9KZkX1gp4HADE3NmVyBOUFJnCtWeT28vL3xVvYJJ93Yn3T5qw/ndu6nmzVAns0CgYEAscdLSseSUK4GOoBRaUUiRnq+9peKgIQIVGsKxSyrmTwyGsLIufnuouGclan8WoS+lvm3U3J//yPGd4pdF9pkCE8Wg52/V8ZjyNio1PdH7RkOhPZZouqUxBAG+ehKT6aLapduW8XpGVWYNdC04/SMBqQRIloKyYRlInRHAi9TWBECgYEAweUzDaStNTVu/+s0L/aSy2+BAMKMI8FnfldUdxsgp/h0DbhItlOwjRDs+ZivyeMTwRoZzoHAcl7WkzhG50Zc9K+PAtUtS8aVas1xNCK1NPuT3GE25lnbrlLZcbubmo4VpsvqevPgxuhyzkkO8glQvRJ8ZD489+ZUwQQPCm+qkNECgYEAr0tVx8tVZsIqCVrn9dIvW75q6lHdKUS71r/rkqz9zpI9TgTCsyG2iKoH/dlD62aP3nEd+O24xBKxlcSItQOm1Po2JwNAFnsdR6fRVwlv4JUfC+TD3moeAHR7bNK68P3N6LpMEqOu7EtNXloe8lKYTF6wN00mPJQWAlDqv9xNkq0=";
        RSAPrivateKey privateKey=getPrivateKey(rsaPrivateKey);
        System.out.println(privateDecrypt(data,privateKey));
    }
}
