package com.pm.bankcards.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

/**
 * Defers Redis cache writes and evictions until commit so rolled-back changes do not affect the cache.
 * Sets a five-minute TTL to clean up unused card versions.
 */
@Configuration
public class CacheConfig {
    @Bean
    RedisCacheManagerBuilderCustomizer transactionAwareCache() {
        return builder ->
                builder
                        .transactionAware()
                        .cacheDefaults(RedisCacheConfiguration
                                .defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5)));
    }
}
