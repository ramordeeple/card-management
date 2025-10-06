package com.pm.bankcards.service.impl;

import com.pm.bankcards.dto.user.UserCreateRequest;
import com.pm.bankcards.dto.user.UserResponse;
import com.pm.bankcards.entity.Role;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import com.pm.bankcards.exception.NotFoundException;
import com.pm.bankcards.repository.RoleRepository;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.service.api.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest req) {
        if (users.existsByUsername(req.username()))
            throw new BusinessException(ErrorCodes.USERNAME_TAKEN, "Username already exists", Map.of("username", req.username()));

        Role role = roles.findByName("USER")
                .orElseThrow(() -> new NotFoundException("User role not found"));

        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRoles(Set.of(role));
        users.save(user);

        return new UserResponse(user.getId(), user.getUsername(), Set.of(role.getName()), user.isEnabled());
    }
}
