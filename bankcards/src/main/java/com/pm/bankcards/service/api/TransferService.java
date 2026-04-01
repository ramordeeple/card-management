package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Transfer;

import java.math.BigDecimal;

/**
 * Service handling financial transactions and balance modifications.
 */
public interface TransferService {

    /**
     * Deposits funds into a card account.
     * * @param cardId    unique identifier of the card
     * @param userId    ID of the user performing the top-up
     * @param amount    the amount to deposit
     * @param requestId unique idempotency key to prevent duplicate transactions
     */
    void topUp(Long cardId, Long userId, BigDecimal amount, String requestId);

    /**
     * Executes a transfer between accounts owned by the same user.
     * * @param req           transfer details (source, destination, amount)
     * @param currentUserId ID of the authenticated user
     * @return {@link Transfer} record containing the transaction status
     */
    Transfer doOwnTransfer(TransferRequest req, Long currentUserId);
}
