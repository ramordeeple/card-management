package com.pm.bankcards.controller;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.dto.transfer.TransferResponse;
import com.pm.bankcards.entity.Transfer;
import com.pm.bankcards.security.AuthUser;
import com.pm.bankcards.service.api.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@Tag(name = "Transfers", description = "Переводы между своими картами")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/transfers")
public class TransferController {
    private final TransferService transfers;

    public TransferController(TransferService transfers) {
        this.transfers = transfers;
    }

    @Operation(summary = "Перевод между своими картами (идемпотентный по requestId")
    @PostMapping("/own")
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
