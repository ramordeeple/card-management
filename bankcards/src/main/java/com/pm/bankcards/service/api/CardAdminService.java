package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.User;


/**
 * Administrative service for card lifecycle management.
 * Provides high-level operations for card creation and state transitions.
 */
public interface CardAdminService {

    /**
     * Creates a new card for a specific user.
     * * @param req   the card creation parameters (e.g., type, currency)
     * @param owner the {@link User} entity who will own the card
     * @return {@link CardResponse} containing the generated card details
     */
    CardResponse create(CardCreateRequest req, User owner);

    /**
     * Administratively blocks a card, preventing any further transactions.
     * * @param cardId unique identifier of the card to block
     */
    void block(Long cardId);

    /**
     * Reactivates a previously blocked or inactive card.
     * * @param cardId unique identifier of the card to activate
     */
    void activate(Long cardId);

    /**
     * Permanently removes a card record from the system.
     * * @param cardId unique identifier of the card to delete
     */
    void delete(Long cardId);
}
