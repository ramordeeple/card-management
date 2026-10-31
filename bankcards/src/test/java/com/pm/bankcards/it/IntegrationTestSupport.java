package com.pm.bankcards.it;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.repository.TransferRepository;
import com.pm.bankcards.repository.UserRepository;
import com.pm.bankcards.security.AuthUser;
import com.pm.bankcards.security.RoleName;
import com.pm.bankcards.service.api.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Containers restart for each subclass, so its Spring context must not be reused afterwards.
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
abstract class IntegrationTestSupport {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6-alpine")
            .withCommand("postgres", "-c", "deadlock_timeout=100ms");
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.4.6-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", postgres::getJdbcUrl);
        properties.add("spring.datasource.username", postgres::getUsername);
        properties.add("spring.datasource.password", postgres::getPassword);
        properties.add("spring.data.redis.host", redis::getHost);
        properties.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        properties.add("security.jwt.secret", () -> Base64.getEncoder().encodeToString(new byte[32]));
        properties.add("security.jwt.expiration-ms", () -> 60000);
        properties.add("security.enc.key", () -> Base64.getEncoder().encodeToString(new byte[32]));
        properties.add("admin.username", () -> "audit-admin");
        properties.add("admin.password", () -> "test-only-password");
        properties.add("test-user.username", () -> "audit-user");
        properties.add("test-user.password", () -> "test-only-password");
    }

    @Autowired CardRepository cards;
    @Autowired UserRepository users;
    @Autowired TransferRepository ledger;
    @Autowired TransferService transfers;
    @Autowired CacheManager caches;
    @Autowired MockMvc mvc;

    User owner;
    User stranger;
    Card from;
    Card to;

    @BeforeEach
    void fixture() {
        ledger.deleteAll();
        cards.deleteAll();
        users.deleteAll();
        caches.getCache("cards").clear();
        owner = createUser("owner");
        stranger = createUser("stranger");
        from = createCard(owner);
        to = createCard(owner);
    }

    User createUser(String username) {
        User result = new User();
        result.setUsername(username);
        result.setPasswordHash("unused-in-test");
        result.setRole(RoleName.USER);
        return users.saveAndFlush(result);
    }

    Card createCard(User cardOwner) {
        Card result = new Card();
        result.setOwner(cardOwner);
        result.setLast4("4242");
        result.setCardNumberEnc(UUID.randomUUID().toString());
        result.setBalance(new BigDecimal("100.00"));
        result.setExpiryMonth(12);
        result.setExpiryYear(2040);
        return cards.saveAndFlush(result);
    }

    AuthUser principal(User account) {
        return new AuthUser(account.getId(), account.getUsername(), "unused-in-test",
                List.of(new SimpleGrantedAuthority("ROLE_USER")), true);
    }

    TransferRequest request(Card source, Card target, String amount) {
        return new TransferRequest(source.getId(), target.getId(), new BigDecimal(amount), UUID.randomUUID().toString());
    }

    BigDecimal balance(Card card) {
        return cards.findById(card.getId()).orElseThrow().getBalance();
    }

    void assertConserved() {
        assertThat(balance(from)).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(balance(to)).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(balance(from).add(balance(to))).isEqualByComparingTo("200.00");
    }
}
