package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.auth.LoginRequest;
import com.pm.bankcards.dto.auth.TokenResponse;
import com.pm.bankcards.dto.card.CardCreateRequest;

public interface AuthService {
    TokenResponse login(LoginRequest req);
}
