package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;

/**
 * Service for managing user profiles and registration.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * * @param req user registration data
     * @return {@link UserResponse} containing created user profile information
     */
    UserResponse create(UserCreateRequest req);
}
