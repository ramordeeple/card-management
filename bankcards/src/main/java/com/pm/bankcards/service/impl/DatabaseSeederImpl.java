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


import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service
public class DatabaseSeederImpl implements DatabaseSeeder {
    private final CardAdminService cardAdminService;
    private final CardNumberProvider cardNumberProvider;
    private final CardQueryService cardQueryService;
    private final MeterRegistry meterRegistry;

    @Value("${app.seeding.bin:440055}")
    private String defaultBin;

    @Override
    public void seedCards(User owner, int count) {
        log.info("Starting heavy generation ({}) cards for user {}", count, owner.getUsername());
        long start = System.currentTimeMillis();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, count).forEach(i -> executor.submit(() -> {
                try {
                    Thread.sleep(10);

                    String rawPan = cardNumberProvider.generate(defaultBin, i);

                    try {
                        cardQueryService.findMyCards(
                                owner.getId(),
                                PageRequest.of(0, 10),
                                new CardFilter(null, null, null));
                    } catch (Exception ignored) {
                    }

                    CardCreateRequest request = new CardCreateRequest(
                            rawPan,
                            12,
                            2034,
                            owner.getId()
                    );

                    cardAdminService.create(request, owner);

                    meterRegistry.counter("cards_seeded_total").increment();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Error while creating card #{}: {}", i, e.getMessage());
                }
            }));
        }

        long duration = System.currentTimeMillis() - start;
        log.info("warmth-up has completed in {} milliseconds", duration);
    }
}
