package com.pm.bankcards.service.impl;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.service.api.CardAdminService;
import com.pm.bankcards.service.api.CardNumberProvider;
import com.pm.bankcards.service.api.CardQueryService;
import com.pm.bankcards.service.api.DatabaseSeeder;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSeederImpl implements DatabaseSeeder {

    private final CardAdminService cardAdminService;
    private final CardQueryService cardQueryService;
    private final CardNumberProvider cardNumberProvider;
    private final MeterRegistry meterRegistry;

    @Value("${app.default-bin:400000}")
    private String defaultBin;

    private static final int CLASSIC_THREAD_COUNT = 20;
    private static final int DB_LATENCY_MS = 10;
    private static final int TERMINATION_TIMEOUT_MIN = 30;

    @Override
    public void seedCards(User owner, int count) {
        log.info("Starting Modern generation (Virtual Threads) for user: {}", owner.getUsername());
        runSeeding(Executors.newVirtualThreadPerTaskExecutor(), owner, count, "virtual");
    }

    @Override
    public void seedCardsClassic(User owner, int count) {
        log.info("Starting Classic generation (Fixed Pool: {}) for user: {}", owner.getUsername(), owner.getUsername());
        runSeeding(Executors.newFixedThreadPool(CLASSIC_THREAD_COUNT), owner, count, "classic");
    }

    @Override
    public void startLoadTest(Long cardId) {
        final int requestCount = 10000;

        Thread.startVirtualThread(() -> {
            log.info("Starting load test for card {}", cardId);

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < requestCount; i++) {
                    executor.submit(() -> {
                        cardQueryService.getCardDetails(cardId);
                    });
                }
            }
        });

        log.info("Load test executed for card for {} requests", requestCount);
    }

    private void runSeeding(ExecutorService executor, User owner, int count, String type) {
        long start = System.currentTimeMillis();

        try (executor) {
            IntStream.range(0, count).forEach(i -> executor.submit(() -> {
                try {
                    Thread.sleep(DB_LATENCY_MS);

                    String rawPan = cardNumberProvider.generate(defaultBin, i);

                    silentlyFindCards(owner.getId());

                    CardCreateRequest request = new CardCreateRequest(rawPan, 12, 2034, owner.getId());
                    cardAdminService.create(request, owner);

                    meterRegistry.counter("cards_seeded_total", "type", type).increment();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Error during {} seeding at index {}: {}", type, i, e.getMessage());
                }
            }));

            if (executor instanceof ThreadPoolExecutor) {
                executor.shutdown();
                executor.awaitTermination(TERMINATION_TIMEOUT_MIN, TimeUnit.MINUTES);
            }
        } catch (InterruptedException e) {
            log.error("{} seeder was interrupted", type);
            Thread.currentThread().interrupt();
        }

        log.info("{} seeding completed in {} ms", type, System.currentTimeMillis() - start);
    }

    private void silentlyFindCards(Long userId) {
        try {
            cardQueryService.findMyCards(userId, PageRequest.of(0, 10), new CardFilter(null, null, null));
            meterRegistry.counter("test_read_operations_total").increment();
        } catch (Exception e) {
            log.warn("Read failed during seeding: {}", e.getMessage());
        }
    }
}
