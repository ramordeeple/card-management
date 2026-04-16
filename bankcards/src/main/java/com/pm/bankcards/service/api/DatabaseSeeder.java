package com.pm.bankcards.service.api;

import com.pm.bankcards.entity.User;

/**
 * Utility service for populating the database and performance testing.
 */
public interface DatabaseSeeder {

    /**
     * Generates and saves a large batch of cards for testing purposes.
     * * @param owner the user who will be assigned the generated cards
     * @param count the number of cards to generate
     */
    void seedCards(User owner, int count);

    /**
     * Generates and saves a batch of cards using a traditional fixed thread pool.
     * <p>
     * This method is designed for performance benchmarking and comparative analysis
     * against {@link #seedCards(User, int)}. It demonstrates how standard OS-level
     * threads (Platform Threads) handle I/O-bound tasks and network latency.
     * </p>
     *
     * @param owner the {@link User} who will be assigned the generated cards
     * @param count the number of cards to generate
     */
    void seedCardsClassic(User owner, int count);

    /**
     * Initiates a high-concurrency load test.
     * Utilizes Virtual Threads to simulate massive traffic against the card system.
     * * @param cardId the card ID to target for repeated queries
     */
    void startLoadTest(Long cardId);
}
