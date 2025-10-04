package com.pm.bankcards.entity;

import jakarta.persistence.*;
import com.pm.bankcards.util.CryptoStringConverter;

import java.math.BigDecimal;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "card_number_enc", nullable = false, unique = true, length = 512)
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

    private BigDecimal balance =  BigDecimal.ZERO;

    @Version
    private long version;


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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
