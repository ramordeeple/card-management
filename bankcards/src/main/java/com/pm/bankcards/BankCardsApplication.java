package com.pm.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
public class BankCardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankCardsApplication.class, args);
    }
}
