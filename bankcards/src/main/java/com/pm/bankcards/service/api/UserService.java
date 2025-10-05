package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;

public interface UserService {
    UserResponse create(UserCreateRequest req);
}
