package com.pm.bankcards.bootstrap;

import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.security.RoleName;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class AdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${admin.username}")
    String adminName;

    @Value("${admin.password}")
    String adminPassword;

    @Value("${test-user.username}")
    String userName;

    @Value("${test-user.password}")
    String rawPassword;

    public AdminBootstrap(UserRepository userRepository,
                          PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByUsername(adminName)
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setUsername(adminName);
                    admin.setPasswordHash(encoder.encode(adminPassword));
                    admin.setRole(RoleName.ADMIN);
                    admin.setEnabled(true);

                    log.info("Admin was successfully created");
                    return userRepository.save(admin);
                });


        userRepository.findByUsername(userName)
                .orElseGet(() -> {
                    User normal = new User();
                    normal.setUsername(userName);
                    normal.setPasswordHash(encoder.encode(rawPassword));
                    normal.setEnabled(true);
                    normal.setRole(RoleName.USER);
                    userRepository.save(normal);

                    log.info("A user for tests was successfully created");
                    return normal;
                });
    }
}