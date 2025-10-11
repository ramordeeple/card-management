package com.pm.bankcards.bootstrap;

import com.pm.bankcards.entity.Role;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.repository.RoleRepository;
import com.pm.bankcards.security.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

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
            admin.setUsername("admin");
            admin.setPasswordHash(encoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            System.out.println("Создан админ: admin/admin123");
            return admin;
        });

        userRepository.findByUsername("user").orElseGet(() -> {
            User normal = new User();
            normal.setUsername("user");
            normal.setPasswordHash(encoder.encode("user123"));
            normal.setEnabled(true);
            normal.setRoles(Set.of(userRole));
            userRepository.save(normal);
            System.out.println("Создан пользователь: user/user123");
            return normal;
        });
    }
}