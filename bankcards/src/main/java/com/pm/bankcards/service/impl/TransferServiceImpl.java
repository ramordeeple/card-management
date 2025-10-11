package com.pm.bankcards.service.impl;


import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Transfer;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import com.pm.bankcards.exception.NotFoundException;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.repository.TransferRepository;
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

    public TransferServiceImpl(TransferRepository transfers,
                               CardRepository cards,
                               List<TransferRule> rules) {
        this.transfers = transfers;
        this.cards = cards;
        this.rules = rules;
    }

    @Override
    public void topUp(Long cardId, Long userId, BigDecimal amount, String requestId) {
        if (transfers.existsByRequestId(requestId))
            throw new BusinessException(ErrorCodes.DUPLICATE_REQUEST, "Дублированный ID запроса");

        Card card = cards.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (!card.getOwner().getId().equals(userId))
            throw new BusinessException(ErrorCodes.CARD_NOT_FOUND_OR_NOT_OWNED, "Card not owned by user");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException(ErrorCodes.AMOUNT_INVALID, "Сумма перевода не может быть меньше или равно 0");

        card.setBalance(card.getBalance().add(amount));

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
        if (transfers.existsByRequestId(req.requestId()))
            return transfers.findByRequestId(req.requestId()).orElseThrow();

        Card from = cards.lockByIdAndOwnerId(req.fromCardId(), currentUserId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.CARD_NOT_FOUND_OR_NOT_OWNED, "Карта отправителя не найдена",
                        Map.of("side", "FROM")));

        Card to = cards.lockByIdAndOwnerId(req.toCardId(), currentUserId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.CARD_NOT_FOUND_OR_NOT_OWNED, "Карта получателя не найдена",
                        Map.of("side", "TO")));

        TransferContext ctx = new TransferContext(from, to, req.amount(), req.requestId());

        for (TransferRule rule : rules) rule.check(ctx);

        from.withdraw(req.amount());
        to.deposit(req.amount());

        Transfer t = new Transfer();
        t.setFromCard(from);
        t.setToCard(to);
        t.setAmount(req.amount());
        t.setRequestId(req.requestId());

        return transfers.save(t);
    }

}
