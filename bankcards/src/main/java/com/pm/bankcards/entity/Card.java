package com.pm.bankcards.entity;

import com.pm.bankcards.security.CardStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class Card {
    /**
     */
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Convert(converter = CryptoStringConverter.class)
    @Setter
    @Column(name = "card_number_enc", nullable = false, unique = true, length = 256)
    private String cardNumberEnc;

    @Setter
    @Getter
    @Column(name = "last4", length = 4, nullable = false)
    private String last4;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Setter
    @Getter
    @Column(name = "expiry_month", nullable = false)
    private int expiryMonth;

    @Setter
    @Getter
    @Column(name = "expiry_year", nullable = false)
    private int expiryYear;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CardStatus status = CardStatus.ACTIVE;

    @Setter
    @Getter
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /************************/

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void block() {
        this.status = CardStatus.BLOCKED;
    }

    public void activate() {
        this.status = CardStatus.ACTIVE;
    }

}
