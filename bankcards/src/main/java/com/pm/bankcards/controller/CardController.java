package com.pm.bankcards.controller;

import com.pm.bankcards.dto.card.*;
import com.pm.bankcards.service.api.ApiStandardResponses;
import com.pm.bankcards.service.api.CardQueryService;
import com.pm.bankcards.service.api.DatabaseSeeder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@ApiStandardResponses
@Slf4j
@Tag(name = "Cards", description = "Operations with own cards")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/api/v1/cards", produces = "application/json")
public class CardController {
    private final CardQueryService cardQueryService;
    private final DatabaseSeeder databaseSeeder;

    public CardController(CardQueryService cardQueryService, DatabaseSeeder databaseSeeder) {
        this.cardQueryService = cardQueryService;
        this.databaseSeeder = databaseSeeder;
    }

    @Operation(summary = "Card list (filters + pagination)")
    @GetMapping
    public Page<CardResponse> findMyCards(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long currentUserId,
            @ParameterObject CardFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return cardQueryService.findMyCards(currentUserId, pageable, filter);
    }

    @Operation(summary = "Card balance")
    @GetMapping("/{id}/balance")
    public BalanceResponse balance(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id")
            Long currentUserId,
            @PathVariable Long id
    ) {
        BigDecimal value = cardQueryService.getBalance(id, currentUserId);
        return new BalanceResponse(id, value);
    }

    @Operation(summary = "Request to block card")
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> requestBlock(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long currentUserId,
            @PathVariable Long id
    ) {
        cardQueryService.requestBlock(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/thread-test")
    public String getThreadInfo() {
        return Thread.currentThread().toString();
    }

    @PostMapping("/load-test/{cardId}")
    public void runLoadTest(@PathVariable Long cardId) {
        databaseSeeder.startLoadTest(cardId);
        log.info("Load test started for {} card", cardId);
    }

    @GetMapping("/{cardId}")
    public CardResponse getCardDetails(@PathVariable Long cardId) {
        return cardQueryService.getCardDetails(cardId);
    }
}
