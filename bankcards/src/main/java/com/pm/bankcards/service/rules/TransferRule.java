package com.pm.bankcards.service.rules;


import java.math.BigDecimal;

public interface TransferRule {
    void check(TransferContext ctx);
}

