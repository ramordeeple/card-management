package com.pm.bankcards.controller;

import com.pm.bankcards.dto.auth.LoginRequest;
import com.pm.bankcards.dto.auth.MeResponse;
import com.pm.bankcards.dto.auth.TokenResponse;
import com.pm.bankcards.security.AuthUser;
import com.pm.bankcards.service.api.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "аутентификация")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @Operation(summary = "Вход в систему (без авторизации)")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }

    @Operation(summary = "Проверка токена (требует авторизации)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthUser me) {
        return new MeResponse(
                me.getId(),
                me.getUsername(),
                me.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse("USER")
                );
    }
}
