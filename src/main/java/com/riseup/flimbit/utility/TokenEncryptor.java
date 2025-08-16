package com.riseup.flimbit.utility;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenEncryptor {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public String encrypt(String token) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(token.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Error encrypting token", ex);
        }
    }

    public String decrypt(String encryptedToken) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
            return new String(original);
        } catch (Exception ex) {
        	System.out.println("failed ");
            throw new RuntimeException("Error decrypting token", ex);
        }
    }
}
