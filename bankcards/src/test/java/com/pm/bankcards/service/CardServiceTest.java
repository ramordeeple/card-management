package com.pm.bankcards.service;

import com.pm.bankcards.dto.card.CardCreateRequest;
import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.entity.CardStatus;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.mapper.CardMapper;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private CardResponse cardResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");

        card = new Card();
        card.setCardNumberEnc("encrypted_1234567890123456");
        card.setLast4("3456");
        card.setOwner(user);
        card.setExpiryMonth(12);
        card.setExpiryYear(2028);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(1000));

        cardResponse = new CardResponse(1L, "**** **** **** 3456",
                user.getUsername(), 12, 2028, CardStatus.ACTIVE, BigDecimal.valueOf(1000));
    }

    @Test
    void createCardSuccess() {
        // given
        CardCreateRequest request = new CardCreateRequest("1234567890123456", 12, 2028, user.getId());
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(any(Card.class))).thenReturn(cardResponse);

        // when
        CardResponse result = cardService.create(request, user);

        // then
        assertThat(result.maskedNumber()).isEqualTo("**** **** **** 3456");
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toDto(any(Card.class));
    }

    @Test
    void findMyCards_withFilterAndPagination() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        CardFilter filter = new CardFilter("3456", CardStatus.ACTIVE, 2028);
        Page<Card> page = new PageImpl<>(Collections.singletonList(card));

        when(cardRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);
        when(cardMapper.toDto(card)).thenReturn(cardResponse);

        Page<CardResponse> result = cardService.findMyCards(user.getId(), pageRequest, filter);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).maskedNumber()).isEqualTo("**** **** **** 3456");
        verify(cardRepository).findAll(any(Specification.class), eq(pageRequest));
    }

    @Test
    void getBalance_success() {
        when(cardRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(card));

        BigDecimal balance = cardService.getBalance(1L, user.getId());

        assertThat(balance).isEqualTo(BigDecimal.valueOf(1000));
        verify(cardRepository).findByIdAndOwnerId(1L, user.getId());
    }

    @Test
    void requestBlockCardSuccess() {
        when(cardRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(card));

        cardService.requestBlock(1L, user.getId());

        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
        verify(cardRepository).findByIdAndOwnerId(1L, user.getId());
    }

    @Test
    void requestBlockCardAlreadyBlockedFails() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.requestBlock(1L, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Card is blocked already");
    }
}