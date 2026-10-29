package com.pm.bankcards.service.impl;


import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Transfer;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import com.pm.bankcards.exception.NotFoundException;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.repository.TransferRepository;
import com.pm.bankcards.service.CardDetailsCache;
import com.pm.bankcards.service.api.TransferService;
import com.pm.bankcards.service.rules.TransferContext;
import com.pm.bankcards.service.rules.TransferRule;
import com.pm.bankcards.entity.Card;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transfers;
    private final CardRepository cards;
    private final List<TransferRule> rules;
    private final CardDetailsCache cache;

    public TransferServiceImpl(TransferRepository transfers,
                               CardRepository cards,
                               List<TransferRule> rules,
                               CardDetailsCache cache) {
        this.transfers = transfers;
        this.cards = cards;
        this.rules = rules;
        this.cache = cache;
    }

    @Override
    public void topUp(Long cardId, Long userId, BigDecimal amount, String requestId) {
        if (transfers.existsByRequestId(requestId))
            throw new BusinessException(
                    ErrorCodes.DUPLICATE_REQUEST,
                    "Duplicated ID request");

        Card card = cards.lockByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (amount == null || amount.signum() <= 0 || amount.stripTrailingZeros().scale() > 2)
            throw new BusinessException(
                    ErrorCodes.AMOUNT_INVALID,
                    "Amount must be positive and have at most two decimal places"
            );

        cache.evict(cardId, card.getVersion());
        card.deposit(amount);

        Transfer t = new Transfer();
        t.setFromCard(null);
        t.setToCard(card);
        t.setAmount(amount);
        t.setRequestId(requestId);
        t.setCreatedAt(Instant.now());

        transfers.save(t);
    }

    @Override
    public Transfer doOwnTransfer(TransferRequest req, Long currentUserId) {
        if (transfers.existsByRequestId(req.requestId())) {
            return getOwnedTransfer(req.requestId(), currentUserId);
        }

        TransferContext context = lockTransferCards(req, currentUserId);
        for (TransferRule rule : rules)
            rule.check(context);

        updateBalances(context);

        return saveTransfer(context);
    }

    private Transfer getOwnedTransfer(String requestId, Long userId) {
        Transfer transfer = transfers.findByRequestId(requestId).orElseThrow();
        if (transfer.getFromCard() == null
                || !transfer.getFromCard().getOwner().getId().equals(userId)
                || !transfer.getToCard().getOwner().getId().equals(userId)) {
            throw new BusinessException(
                    ErrorCodes.CARD_NOT_FOUND_OR_NOT_OWNED,
                    "Transfer not owned by user");
        }

        return transfer;
    }

    private TransferContext lockTransferCards(TransferRequest req, Long userId) {
        Card from;
        Card to;
        if (req.fromCardId().compareTo(req.toCardId()) <= 0) {
            from = lockCard(req.fromCardId(), userId, "FROM");
            to = lockCard(req.toCardId(), userId, "TO");
        } else {
            to = lockCard(req.toCardId(), userId, "TO");
            from = lockCard(req.fromCardId(), userId, "FROM");
        }

        return new TransferContext(from, to, req.amount(), req.requestId());
    }

    private void updateBalances(TransferContext context) {
        Card from = context.from();
        Card to = context.to();
        cache.evict(from.getId(), from.getVersion());
        cache.evict(to.getId(), to.getVersion());
        from.withdraw(context.amount());
        to.deposit(context.amount());
    }

    private Transfer saveTransfer(TransferContext context) {
        Transfer transfer = new Transfer();
        transfer.setFromCard(context.from());
        transfer.setToCard(context.to());
        transfer.setAmount(context.amount());
        transfer.setRequestId(context.requestId());

        return transfers.save(transfer);
    }

    private Card lockCard(Long cardId, Long userId, String side) {
        return cards.lockByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.CARD_NOT_FOUND_OR_NOT_OWNED,
                        "FROM".equals(side) ?
                                "Card sender not found" :
                                "Card receiver not found",
                        Map.of("side", side)));
    }

}
