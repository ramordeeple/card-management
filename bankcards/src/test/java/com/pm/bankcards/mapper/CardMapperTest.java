package com.pm.bankcards.mapper;

import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.CardStatus;
import com.pm.bankcards.entity.User;
import org.junit.jupiter.api.Test;
import com.pm.bankcards.entity.Card;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CardMapperTest {

    private final CardMapper mapper = new CardMapper();

    @Test
    void toDtoReturnsNullWhenCardIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDtoMasksNumberAndCopiesBasicFields() {
        Card card = new Card();

        card.setExpiryMonth(12);
        card.setExpiryYear(2030);
        card.setLast4("1234");
        card.activate();
        card.deposit(new BigDecimal("100.50"));

        User owner = new User();
        owner.setUsername("Alice");
        card.setOwner(owner);

        CardResponse dto = mapper.toDto(card);

        assertThat(dto).isNotNull();
        assertThat(dto.maskedNumber()).isEqualTo("**** **** **** 1234");
        assertThat(dto.ownerUsername()).isEqualTo("Alice");
        assertThat(dto.expiryMonth()).isEqualTo(12);
        assertThat(dto.expiryYear()).isEqualTo(2030);
        assertThat(dto.status()).isEqualTo(CardStatus.ACTIVE);
        assertThat(dto.balance()).isEqualByComparingTo("100.50");
    }

    @Test
    void toDtoMasksUnknownLast4AsQuestionMarks() {
        Card card = new Card();
        card.setLast4(null);
        User owner = new User();
        owner.setUsername("Bob");
        card.setOwner(owner);

        CardResponse dto = mapper.toDto(card);
        assertThat(dto.maskedNumber()).isEqualTo("**** **** **** ????");
    }
}
