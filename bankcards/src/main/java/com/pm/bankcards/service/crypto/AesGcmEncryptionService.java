package com.pm.bankcards.service.crypto;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesGcmEncryptionService implements  EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec keySpec;
    private final SecureRandom random = new SecureRandom();

    public AesGcmEncryptionService(@Value("${security.enc.key}") String keyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }


    @Override
    public String encrypt(String value) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] cipherText = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] result = new byte[IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, result, 0, IV_LENGTH);
            System.arraycopy(cipherText, 0, result, IV_LENGTH, cipherText.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new IllegalStateException("Could not encrypt data", e);
        }
    }

    @Override
    public String decrypt(String value) {
        try {
            byte[] decoded = Base64.getDecoder().decode(value);

            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);

            byte[] cipherText = new byte[decoded.length - IV_LENGTH];
            System.arraycopy(decoded, IV_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] plain = cipher.doFinal(cipherText);

            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not decrypt data", e);
        }
    }
}
