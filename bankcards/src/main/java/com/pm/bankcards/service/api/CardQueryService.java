package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.dto.card.CardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardQueryService {
    Page<CardResponse> findMyCards(Long currentUserId, Pageable pageable, CardFilter filter);
    BigDecimal getBalance(Long cardId, Long currentUserId);
    void requestBlock(Long cardId, Long currentUserId);
}
