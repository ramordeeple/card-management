package com.pm.bankcards.service.impl;


import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Transfer;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.repository.TransferRepository;
import com.pm.bankcards.service.api.TransferService;
import com.pm.bankcards.service.rules.TransferContext;
import com.pm.bankcards.service.rules.TransferRule;
import com.pm.bankcards.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


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
    public Transfer doOwnTransfer(TransferRequest req, Long currentUserId) {
        if (transfers.existsByRequestId(req.requestId()))
            return transfers.findByRequestId(req.requestId()).orElseThrow();

        Card from = cards.lockByIdAndOwnerId(req.fromCardId(), currentUserId)
                .orElseThrow(() -> new BusinessException("card_not_found", "Sender's card not found", Map.of("side", "FROM")));

        Card to = cards.lockByIdAndOwnerId(req.toCardId(), currentUserId)
                .orElseThrow(() -> new BusinessException("card_not_found", "Receiver's card not found", Map.of("side", "TO")));

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
