package com.pm.bankcards.service.impl;

import com.pm.bankcards.dto.auth.LoginRequest;
import com.pm.bankcards.dto.auth.TokenResponse;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.security.JwtTokenService;
import com.pm.bankcards.service.api.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwt;

    public AuthServiceImpl(UserRepository users, PasswordEncoder encoder, JwtTokenService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = users.findByUsername(req.username())
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.INVALID_CREDENTIALS,
                        "Incorrect credentials",
                        Map.of("username", req.username())
                ));
        if (!encoder.matches(req.password(), user.getPasswordHash()))
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Incorrect credentials");

        if (!user.isEnabled())
            throw new BusinessException(ErrorCodes.USER_DISABLED, "User account is disabled",
                    Map.of("username", user.getUsername()));

        var token = jwt.generate(
                user.getUsername(),
                user.getRoles().stream().map(r -> r.getName()).toList()
        );
        return new TokenResponse(token);
    }
}
