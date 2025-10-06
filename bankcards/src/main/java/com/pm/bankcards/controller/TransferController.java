package com.pm.bankcards.controller;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.dto.transfer.TransferResponse;
import com.pm.bankcards.entity.Transfer;
import com.pm.bankcards.security.AuthUser;
import com.pm.bankcards.service.api.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    private final TransferService transfers;

    public TransferController(TransferService transfers) {
        this.transfers = transfers;
    }

    public TransferResponse ownTransfer(
            @AuthenticationPrincipal AuthUser me,
            @Valid @RequestBody TransferRequest req)
    {
        Transfer t = transfers.doOwnTransfer(req, me.getId());

        return new TransferResponse(
                t.getId(),
                "**** **** **** " + t.getFromCard().getLast4(),
                "**** **** **** " + t.getToCard().getLast4(),
                t.getAmount(),
                t.getCreatedAt() != null ? t.getCreatedAt() : Instant.now()
        );
    }
}
