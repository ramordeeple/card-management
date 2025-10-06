package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.User;

public interface CardAdminService {
    CardResponse create(CardCreateRequest req, User owner);
    void block(Long cardId);
    void activate(Long cardId);
}
