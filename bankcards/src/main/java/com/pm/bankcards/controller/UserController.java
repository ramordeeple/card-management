package com.pm.bankcards.controller;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;
import com.pm.bankcards.service.api.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Admin Users", description = "Управление пользователями (админ)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @Operation(summary = "Создать пользователя")
    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest req) {
        return users.create(req);
    }
}
