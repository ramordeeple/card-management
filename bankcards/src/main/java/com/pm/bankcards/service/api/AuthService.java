package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.auth.LoginRequest;
import com.pm.bankcards.dto.auth.TokenResponse;
import com.pm.bankcards.dto.card.CardCreateRequest;

/**
 * Service for user authentication and session management.
 * Handles credential verification and issuance of security tokens.
 */
public interface AuthService {

    /**
     * Authenticates a user based on provided credentials.
     * * @param req the login request containing username and password
     * @return {@link TokenResponse} containing access and refresh tokens
     */
    TokenResponse login(LoginRequest req);
}
