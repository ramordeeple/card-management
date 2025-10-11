package com.pm.bankcards.bootstrap;

import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.findByUsername("admin").ifPresentOrElse(
                u -> {},
                () -> {
                    User u = new User();
                    u.setUsername("admin");
                    u.setPasswordHash(passwordEncoder.encode("admin123"));
                    u.setEnabled(true);
                    userRepository.save(u);
                    System.out.println("Создан пользователь admin/admin123");
                }
        );

        userRepository.findByUsername("user").ifPresentOrElse(
                u -> {},
                () -> {
                    User normal = new User();
                    normal.setUsername("user");
                    normal.setPasswordHash(passwordEncoder.encode("user123"));
                    normal.setEnabled(true);
                    userRepository.save(normal);
                    System.out.println("Создан пользователь user/user123");
                }
        );
    }
}
