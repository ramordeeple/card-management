package com.pm.bankcards.service.crypto;

public interface EncryptionService {
    String encrypt(String value);
    String decrypt(String value);
}
