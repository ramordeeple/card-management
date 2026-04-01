package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.dto.card.CardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Service for read-only card operations and status inquiries.
 * Optimized for high performance using caching strategies.
 */
public interface CardQueryService {

    /**
     * Retrieves a paginated list of cards belonging to the current user.
     * * @param currentUserId the ID of the authenticated user
     * @param pageable      pagination and sorting parameters
     * @param filter        filtering criteria (e.g., status, type)
     * @return a {@link Page} of card details
     */
    Page<CardResponse> findMyCards(Long currentUserId, Pageable pageable, CardFilter filter);

    /**
     * Retrieves the current balance for a specific card.
     * * @param cardId        unique identifier of the card
     * @param currentUserId ID of the user requesting the balance (for security check)
     * @return the current balance as {@link BigDecimal}
     */
    BigDecimal getBalance(Long cardId, Long currentUserId);

    /**
     * Submits a user-initiated request to block their own card.
     * * @param cardId        unique identifier of the card
     * @param currentUserId ID of the card owner
     */
    void requestBlock(Long cardId, Long currentUserId);

    /**
     * Retrieves full details of a card.
     * Implementation typically uses Redis caching for high-load optimization.
     * * @param cardId unique identifier of the card
     * @return {@link CardResponse} with detailed information
     */
    CardResponse getCardDetails(Long cardId);
}
