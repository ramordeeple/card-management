package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.NotFoundException;

/**
 * Service for managing user profiles and registration.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * * @param req user registration data
     *
     * @return {@link UserResponse} containing created user profile information
     */
    UserResponse create(UserCreateRequest req);

    /**
     * Получает полную сущность пользователя по его ID.
     * Используется для передачи владельца в методы генерации карт.
     *
     * @param id идентификатор пользователя
     * @return сущность {@link User}
     * @throws NotFoundException если пользователь не найден
     */
    User getById(Long id);

    /**
     * Получает сущность текущего авторизованного пользователя из Security Context.
     *
     * @return сущность {@link User}
     */
    User getCurrentUser();
}
