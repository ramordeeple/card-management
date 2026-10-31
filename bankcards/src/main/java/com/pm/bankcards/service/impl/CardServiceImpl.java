package com.pm.bankcards.service.impl;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.security.CardStatus;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import com.pm.bankcards.exception.NotFoundException;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.repository.spec.CardSpecifications;
import com.pm.bankcards.service.CardDetailsCache;
import com.pm.bankcards.service.api.CardAdminService;
import com.pm.bankcards.service.api.CardQueryService;
import com.pm.bankcards.service.crypto.EncryptionService;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.mapper.CardMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CardServiceImpl implements CardQueryService, CardAdminService {

    private final CardRepository cards;
    private final CardMapper mapper;
    private final EncryptionService encryption;
    private final CardDetailsCache cache;

    public CardServiceImpl(CardRepository cards, CardMapper mapper, EncryptionService encryption, CardDetailsCache cache) {
        this.cards = cards;
        this.mapper = mapper;
        this.encryption = encryption;
        this.cache = cache;
    }

    @Transactional
    @Override
    public Page<CardResponse> findMyCards(Long currentUserId, Pageable pageable, CardFilter filter) {
        var status = filter != null ? filter.status() : null;

        var last4 = (filter != null && filter.last4() != null &&
                !filter.last4().isBlank()) ?
                filter.last4() :
                null;

        var expiryYear = (filter != null) ? filter.expiryYear() : null;

        Specification<Card> spec = Specification.allOf(
                CardSpecifications.ownerId(currentUserId),
                CardSpecifications.status(status),
                CardSpecifications.last4(last4),
                CardSpecifications.expiryYear(expiryYear)
        );

        return cards.findAll(spec, pageable).map(mapper::toDto);
    }

    @Transactional
    @Override
    public BigDecimal getBalance(Long cardId, Long currentUserId) {
        return cards.findByIdAndOwnerId(cardId, currentUserId)
                .map(Card::getBalance)
                .orElseThrow(() -> new NotFoundException("Card not found or does not belong to you"));
    }

    @Transactional
    public void requestBlock(Long cardId, Long currentUserId) {
        Card card = cards.lockByIdAndOwnerId(cardId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Card not found or does not belong to you"));

        if (card.getStatus() == CardStatus.BLOCKED)
            throw new BusinessException(ErrorCodes.CARD_ALREADY_BLOCKED, "Card is blocked already", Map.of("cardId", cardId));

        cache.evict(cardId, card.getVersion());
        card.block();
    }

    @Override
    public CardResponse getCardDetails(Long cardId, Long currentUserId) {
        // This owner check runs on cache hits too. A new version cannot use a late, stale cache fill.
        long version = cards.findVersionByIdAndOwnerId(cardId, currentUserId)
                .orElseThrow(() -> new NotFoundException("Card not found or does not belong to you"));

        return cache.get(cardId, version);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CardResponse create(CardCreateRequest req, User owner) {
        Card card = new Card();
        String pan = req.number().replaceAll("\\s+", "");
        card.setOwner(owner);
        card.setExpiryMonth(req.expiryMonth());
        card.setExpiryYear(req.expiryYear());
        card.setLast4(req.number().substring(req.number().length() - 4));
        card.setCardNumberEnc(encryption.encrypt(pan));

        cards.save(card);
        return mapper.toDto(card);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void block(Long cardId) {
        Card card = cards.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        cache.evict(cardId, card.getVersion());
        card.block();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void activate(Long cardId) {
        Card card = cards.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        cache.evict(cardId, card.getVersion());
        card.activate();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long cardId) {
        Card card = cards.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        cache.evict(cardId, card.getVersion());
        cards.delete(card);
    }

}
