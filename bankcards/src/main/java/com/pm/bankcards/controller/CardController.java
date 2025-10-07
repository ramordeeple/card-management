package com.pm.bankcards.controller;

import com.pm.bankcards.dto.card.BalanceResponse;
import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.pm.bankcards.service.api.*;

import java.math.BigDecimal;

@Tag(name = "Cards", description = "операции с собственными картами")
@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardQueryService cards;

    public CardController(CardQueryService cards) {
        this.cards = cards;
    }

    @Operation(summary = "Список карт (фильтры + пагинация)")
    @GetMapping
    public Page<CardResponse> findMyCards(
            @AuthenticationPrincipal AuthUser me,
            CardFilter filter,
            Pageable pageable
            ) {
        return cards.findMyCards(me.getId(), pageable, filter);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse balance(
            @AuthenticationPrincipal AuthUser me,
            @PathVariable Long id
    ) {
        BigDecimal value = cards.getBalance(id, me.getId());
        return new BalanceResponse(id, value);
    }

    @GetMapping("/{id}/block")
    public void requestBlock(
            @AuthenticationPrincipal AuthUser me,
            @PathVariable @NotNull Long id
    ) {
        cards.requestBlock(id, me.getId());
    }
}
