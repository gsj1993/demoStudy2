package com.example.demoStudy2.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


@Slf4j
public class SecretUtil {

    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    /**
     * AES GCM encryption
     * @param contentByte - Bytes of content to be encrypted
     * @param key - CEK (AES 256)
     * @return
     */
    public static String encrypt(byte[] contentByte, byte[] key) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        byte[] encrypted = null;
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[12];
            // do not reuse iv with the same key
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);
            encrypted = cipher.doFinal(contentByte);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encrypted.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);
            byte[] cipherMessage = byteBuffer.array();
            return Base64.encodeBase64String(cipherMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Encryption error: {}", e);
            throw e;
        }
    }


    /**
     * AES GCM decryption
     * @param content - Bytes of content to be decrypted
     * @param key - CEK (AES 256)
     * @return
     */
    public static String decrypt(byte[] content, byte[] key) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        ByteBuffer byteBuffer = ByteBuffer.wrap(content);
        int ivLength = byteBuffer.getInt();
        if(ivLength != 12) {
            throw new IllegalArgumentException("invalid iv length");
        }
        try {
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);
            Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, parameterSpec);
            byte[] original = cipher.doFinal(cipherText);
            return new String(original);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
           | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {
            throw e;
        }
    }
}
