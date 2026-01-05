package com.practice.stomp.utils;

import com.practice.stomp.config.properties.CipherProperties;
import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@UtilityClass
public class CipherUtils {
    public static String encrypt(String plainText) {
        try {
            Cipher cipher =  Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(mysqlAes128Key(), "AES"));

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String decrypt(String encryptedHex) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(mysqlAes128Key(), "AES"));

            byte[] decrypted = cipher.doFinal(hexToBytes(encryptedHex));

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] mysqlAes128Key() throws Exception {
        byte[] sha512 = MessageDigest.getInstance("SHA-512")
                .digest(CipherProperties.seed().getBytes(StandardCharsets.UTF_8));

        byte[] foldedKey = new byte[16]; // AES-128

        for (int i = 0; i < sha512.length; i++) {
            foldedKey[i % 16] ^= sha512[i];
        }

        return foldedKey;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
