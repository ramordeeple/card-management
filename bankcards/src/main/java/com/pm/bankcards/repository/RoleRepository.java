package com.pm.bankcards.repository;

import com.pm.bankcards.entity.Role;
import com.pm.bankcards.security.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName name);
}
