package com.pm.bankcards.controller;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.service.api.CardAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Admin cards", description = "Администрирование карт")
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "Создать карту пользователя")
    @PostMapping
    public CardResponse create(@Valid @RequestBody CardCreateRequest req) {
        User owner = users.findById(req.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return cards.create(req, owner);
    }

    @Operation(summary = "Активировать карту (админ)")
    @PostMapping("/{id}/activate")
    public void activate(@PathVariable Long id) {
        cards.activate(id);
    }

    @Operation(summary = "Заблокировать карту (админ)")
    @PostMapping("/{id}/block")
    public void block(@PathVariable Long id) {
        cards.block(id);
    }

    @Operation(summary = "Удалить карту (админ)")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cards.delete(id);
    }
}
