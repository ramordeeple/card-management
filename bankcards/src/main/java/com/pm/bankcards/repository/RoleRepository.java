package com.pm.bankcards.repository;

import com.pm.bankcards.entity.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
}
