package com.pm.bankcards.entity;

import jakarta.persistence.*;
import com.pm.bankcards.util.CryptoStringConverter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "card_number_enc", nullable = false, unique = true, length = 256)
    private String cardNumberEnc;

    @Column(name = "last4", length = 4, nullable = false)
    private String last4;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "expiry_month", nullable = false)
    private int expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private int expiryYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CardStatus status = CardStatus.ACTIVE;

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

    public boolean isExpired() {
        YearMonth expiry = YearMonth.of(expiryYear, expiryMonth);
        return expiry.isBefore(YearMonth.now());
    }

    /************************/
    public Long getId() {
        return id;
    }

    public String getCardNumberEnc() {
        return cardNumberEnc;
    }

    public void setCardNumberEnc(String cardNumberEnc) {
        this.cardNumberEnc = cardNumberEnc;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
