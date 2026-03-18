package com.pm.bankcards.service.impl;

import com.pm.bankcards.service.api.CardNumberProvider;

public class CardNumberGeneratorServiceImpl implements CardNumberProvider {
    @Override
    public String generate(String bin, int index) {
        String partial = bin + String.format("%09d", index);
        return partial + calculateCheckDigit(partial);
    }

    private int calculateCheckDigit(String partial) {
        int sum = 0;
        for (int i = 0; i < partial.length(); i++) {
            int digit = Character.getNumericValue(partial.charAt(i));

            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }

            sum += digit;
        }

        var result = (10 - (sum % 10)) % 10;
        return result;
    }
}
