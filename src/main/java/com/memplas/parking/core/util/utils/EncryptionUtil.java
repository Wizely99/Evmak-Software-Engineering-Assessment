package com.memplas.parking.core.util.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private final BytesEncryptor encryptor;

    private final String SECRET_KEY;

    // Load values from application.properties or environment variables
    public EncryptionUtil(
            @Value("${encryption.secret-key}") String secretKey,
            @Value("${encryption.salt}") String salt) {
        this.SECRET_KEY = secretKey;
        this.encryptor = new AesBytesEncryptor(secretKey, salt);
    }

    public String encrypt(String data) {
        byte[] encrypted = encryptor.encrypt(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedData) {
        byte[] decrypted = encryptor.decrypt(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // Method to generate HMAC signature for given data
    public String generateHmacSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256"); // Use HMAC with SHA-256
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder()
                .encodeToString(hmacBytes); // Return the signature as Base64 encoded string
    }

    // Method to verify if HMAC signature matches
    public boolean verifyHmacSignature(String data, String expectedSignature) throws Exception {
        String generatedSignature = generateHmacSignature(data);
        return generatedSignature.equals(
                expectedSignature); // Compare generated signature with the expected signature
    }
}
