package com.pm.bankcards.service.rules;


public interface TransferRule {
    void check(TransferContext ctx);
}

