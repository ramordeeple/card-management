package com.pm.bankcards.service.api;

public interface CardNumberProvider {
    String generate (String bin, int index);
}
