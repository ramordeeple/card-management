package com.pm.bankcards.it;

import com.pm.bankcards.entity.Card;
import com.pm.bankcards.mapper.CardMapper;
import com.pm.bankcards.security.AuthUser;
import com.pm.bankcards.security.CardStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardCacheIT extends IntegrationTestSupport {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    TransactionTemplate transaction;
    @SpyBean
    CardMapper mapper;

    Set<String> cachedKeys() {
        return redisTemplate.keys("cards::*");
    }

    void warmCache(Card card) throws Exception {
        mvc.perform(get("/api/v1/cards/" + card.getId()).with(user(principal(owner))))
                .andExpect(status().isOk());
    }

    @Test
    void committedTransferMustRefreshRedisCardDetails() throws Exception {
        mvc.perform(get("/api/v1/cards/" + from.getId())
                        .with(user(principal(owner))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.0));

        warmCache(to);

        Set<String> before = cachedKeys();
        assertThat(before).hasSize(2);

        transfers.doOwnTransfer(request(from, to, "10.00"), owner.getId());

        assertThat(balance(from)).isEqualByComparingTo("90.00");
        assertThat(balance(to)).isEqualByComparingTo("110.00");
        assertThat(cachedKeys()).doesNotContainAnyElementsOf(before);

        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(90.0));

        mvc.perform(get("/api/v1/cards/" + to.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(110.0));
    }

    @Test
    void topUpMustCommitAndInvalidateRedis() throws Exception {
        warmCache(from);
        Set<String> before = cachedKeys();
        assertThat(before).hasSize(1);

        transfers.topUp(
                from.getId(),
                owner.getId(),
                BigDecimal.TEN,
                UUID.randomUUID().toString());

        assertThat(balance(from)).isEqualByComparingTo("110.00");
        assertThat(cachedKeys()).doesNotContainAnyElementsOf(before);

        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(110.0));
    }

    @Test
    void evictionIsDeferredUntilOuterTransactionCommits() throws Exception {
        warmCache(from);
        warmCache(to);
        Set<String> before = cachedKeys();

        transaction.executeWithoutResult(tx -> {
            transfers.doOwnTransfer(request(from, to, "10.00"), owner.getId());
            assertThat(cachedKeys()).containsExactlyInAnyOrderElementsOf(before);
        });

        assertThat(cachedKeys()).doesNotContainAnyElementsOf(before);
        assertThat(balance(from)).isEqualByComparingTo("90.00");
    }

    @Test
    void rollbackPreservesCacheAndBalances() throws Exception {
        warmCache(from);
        warmCache(to);

        Set<String> before = cachedKeys();
        assertThat(before).hasSize(2);

        transaction.executeWithoutResult(tx -> {
            transfers.doOwnTransfer(request(from, to, "10.00"), owner.getId());
            assertThat(cachedKeys()).containsExactlyInAnyOrderElementsOf(before);
            tx.setRollbackOnly();
        });

        assertThat(cachedKeys()).containsExactlyInAnyOrderElementsOf(before);
        assertThat(balance(from)).isEqualByComparingTo("100.00");
        assertThat(balance(to)).isEqualByComparingTo("100.00");
        assertThat(ledger.count()).isZero();

        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    void lateCacheFillCannotMakeNextReadReturnOldBalance() throws Exception {
        Long sourceCardId = from.getId();
        String cardDetailsUrl = "/api/v1/cards/" + sourceCardId;

        CompletableFuture<Void> oldBalanceLoaded = new CompletableFuture<>();
        CompletableFuture<Void> allowOldReadToFinish = new CompletableFuture<>();

        // Первое чтение задерживаем после загрузки баланса; последующие выполняем как обычно.
        doAnswer(invocation -> {
            var cardDetails = invocation.callRealMethod();
            oldBalanceLoaded.complete(null);
            allowOldReadToFinish.get(10, TimeUnit.SECONDS);

            return cardDetails;
        }).doCallRealMethod()
                .when(mapper).toDto(argThat(card -> sourceCardId.equals(card.getId())));

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var delayedRead = executor.submit(() ->
                    mvc.perform(get(cardDetailsUrl).with(user(principal(owner))))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.balance").value(100.0))
                            .andReturn());

            oldBalanceLoaded.get(10, TimeUnit.SECONDS);
            transfers.doOwnTransfer(request(from, to, "10.00"), owner.getId());
            allowOldReadToFinish.complete(null);
            delayedRead.get(10, TimeUnit.SECONDS);
        } finally {
            // При ошибке тоже освобождаем чтение, затем дожидаемся завершения потока.
            allowOldReadToFinish.complete(null);
        }

        // Старый запрос уже завершён; новое чтение должно вернуть актуальный баланс.
        mvc.perform(get(cardDetailsUrl).with(user(principal(owner))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(90.0));
    }

    @Test
    void userBlockPersistsAndInvalidatesCachedStatus() throws Exception {
        warmCache(from);
        mvc.perform(post("/api/v1/cards/" + from.getId() + "/block").with(user(principal(owner))))
                .andExpect(status().isNoContent());

        assertThat(cards.findById(from.getId())
                .orElseThrow().getStatus())
                .isEqualTo(CardStatus.BLOCKED);

        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void adminStatusChangesAndDeletionInvalidateCache() throws Exception {
        AuthUser admin = new AuthUser(
                owner.getId(),
                "admin", "unused",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")), true);

        warmCache(from);


        /// BLOCK {
        mvc.perform(post("/api/v1/admin/cards/" + from.getId() + "/block").with(user(admin)))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("BLOCKED"));
        /// BLOCK }

        /// ACTIVATE {
        mvc.perform(post("/api/v1/admin/cards/" + from.getId() + "/activate").with(user(admin)))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ACTIVE"));
        /// ACTIVATE }

        /// DELETE
        mvc.perform(delete("/api/v1/admin/cards/" + from.getId()).with(user(admin)))
                .andExpect(status().isOk());

        /// GET
        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isNotFound());
    }
}
