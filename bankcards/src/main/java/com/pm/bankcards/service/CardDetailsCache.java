package com.pm.bankcards.service;

import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.exception.NotFoundException;
import com.pm.bankcards.mapper.CardMapper;
import com.pm.bankcards.repository.CardRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardDetailsCache {
    private final CardRepository cards;
    private final CardMapper mapper;

    public CardDetailsCache(CardRepository cards, CardMapper mapper) {
        this.cards = cards;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "#cardId + ':' + #version")
    public CardResponse get(Long cardId, long version) {
        return cards.findById(cardId).map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    /**
     * Requests eviction of the cached card version.
     * The body is empty because Spring's proxy processes @CacheEvict.
     * CacheConfig defers eviction until the active transaction commits; rollback cancels it.
     */
    @CacheEvict(value = "cards", key = "#cardId + ':' + #version")
    public void evict(Long cardId, long version) {}
}
