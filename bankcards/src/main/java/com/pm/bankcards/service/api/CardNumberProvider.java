package com.pm.bankcards.service.api;

/**
 * Component responsible for generating unique card identifiers (PAN).
 * Ensures compliance with banking numbering formats.
 */
public interface CardNumberProvider {

    /**
     * Generates a valid card number based on Bank Identification Number (BIN).
     * * @param bin   the first digits representing the bank/branch
     * @param index a unique sequence number to ensure uniqueness
     * @return a formatted card number string
     */
    String generate(String bin, int index);
}
