package com.pm.bankcards.controller;

import com.pm.bankcards.entity.User;
import com.pm.bankcards.service.api.DatabaseSeeder;
import com.pm.bankcards.service.api.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/debug")
@RequiredArgsConstructor
@Tag(name = "Debug & Load Testing", description = "Управление наполнением базы и нагрузочными тестами")
public class DebugController {

    private final DatabaseSeeder databaseSeeder;
    private final UserService userService;

    @Operation(summary = "Массовая генерация карт (Java 21 Virtual Threads)")
    @PostMapping("/seed-modern")
    public String seedModern(
            @RequestParam(required = false) Long userId, // Добавили ID
            @RequestParam(defaultValue = "10000") int count) {

        User owner = (userId != null) ? userService.getById(userId) : userService.getCurrentUser();

        databaseSeeder.seedCards(owner, count);
        return String.format("Modern seeding started for %d cards (User: %s)", count, owner.getUsername());
    }

    @Operation(summary = "Массовая генерация карт (Classic Threads Pool)")
    @PostMapping("/seed-classic")
    public String seedClassic(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "10000") int count) {

        User owner = (userId != null) ? userService.getById(userId) : userService.getCurrentUser();

        databaseSeeder.seedCardsClassic(owner, count);
        return String.format("Classic seeding started for %d cards (User: %s)", count, owner.getUsername());
    }
}