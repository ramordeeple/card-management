package com.pm.bankcards.controller;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;
import com.pm.bankcards.service.api.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest req) {
        return users.create(req);
    }
}
