package com.pm.bankcards.service.impl;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import com.pm.bankcards.exception.NotFoundException;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.security.RoleName;
import com.pm.bankcards.service.api.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest req) {
        if (users.existsByUsername(req.username()))
            throw new BusinessException(
                    ErrorCodes.USERNAME_TAKEN,
                    "Username already exists",
                    Map.of("username", req.username())
            );

        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRole(RoleName.USER);

        var savedUser = users.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                savedUser.isEnabled()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED, "This user is not authorized");
        }

        String username = authentication.getName();

        return users.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("This user: " + username + " not found"));
    }
}
