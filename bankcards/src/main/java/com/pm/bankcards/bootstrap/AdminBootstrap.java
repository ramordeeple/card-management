package com.pm.bankcards.bootstrap;

import com.pm.bankcards.entity.Role;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.repository.RoleRepository;
import com.pm.bankcards.security.RoleName;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class AdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Value("${ADMIN_NAME}")
    String adminName;

    @Value("${ADMIN_NAME}")
    String adminPassword;

    @Value("${TEST_USERNAME}")
    String userName;

    @Value("${TEST_USER_PASSWORD}")
    String rawPassword;

    public AdminBootstrap(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(Role.of(RoleName.ADMIN)));

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(Role.of(RoleName.USER)));


        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User();
            admin.setUsername(adminName);
            admin.setPasswordHash(encoder.encode(adminPassword));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            log.info("Админ ({} | {}) успешно создан", adminName, adminPassword);

            return admin;
        });

        userRepository.findByUsername("user").orElseGet(() -> {
            User normal = new User();
            normal.setUsername(userName);
            normal.setPasswordHash(encoder.encode(rawPassword));
            normal.setEnabled(true);
            normal.setRoles(Set.of(userRole));
            userRepository.save(normal);

            log.info("Обычный пользователь для тестов ({} | {}) успешно создан", userName, rawPassword);

            return normal;
        });
    }
}