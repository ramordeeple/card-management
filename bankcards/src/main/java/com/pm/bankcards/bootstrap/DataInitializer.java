//package com.pm.bankcards.bootstrap;
//
//import com.pm.bankcards.entity.User;
//import com.pm.bankcards.repository.CardRepository;
//import com.pm.bankcards.repository.UserRepository;
//import com.pm.bankcards.service.api.DatabaseSeeder;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//@Order(2)
//public class DataInitializer implements CommandLineRunner {
//    private final CardRepository cardRepository;
//    private final UserRepository userRepository;
//    private final DatabaseSeeder seeder;
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        User normalUser = userRepository.findByUsername("user")
//                .orElseThrow(() -> new RuntimeException("Юзер не найден"));
//
//        if (cardRepository.count() < 100) {
//            seeder.seedCards(normalUser, 10_000);
//        }
//    }
//}
