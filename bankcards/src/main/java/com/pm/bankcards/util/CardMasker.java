package com.pm.bankcards.util;

public final class CardMasker {

    private CardMasker() {}

    public static String mask(String last4) {
        return "**** **** **** " + (last4 == null ? "????" : last4);
    }
}
