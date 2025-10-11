package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Transfer;

import java.math.BigDecimal;

public interface TransferService {
    void topUp(Long cardId, Long userId, BigDecimal amount, String requestId);
    Transfer doOwnTransfer(TransferRequest req, Long currentUserId);
}
