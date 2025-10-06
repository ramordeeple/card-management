package com.pm.bankcards.controller;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.service.api.CardAdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardAdminService cards;
    private final UserRepository users;

    public AdminCardController(CardAdminService cards, UserRepository users) {
        this.cards = cards;
        this.users = users;
    }

    @PostMapping
    public CardResponse create(@Valid @RequestBody CardCreateRequest req) {
        User owner = users.findById(req.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return cards.create(req, owner);
    }

    @PostMapping("/{id}/activate")
    public void activate(@PathVariable Long id) {
        cards.activate(id);
    }

    @PostMapping("/{id}/block")
    public void block(@PathVariable Long id) {
        cards.block(id);
    }
}
