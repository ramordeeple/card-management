package com.pm.bankcards.it;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationIT extends IntegrationTestSupport {
    @Test
    void contextLoads() {
        assertThat(caches.getCache("cards")).isNotNull();
    }
}
