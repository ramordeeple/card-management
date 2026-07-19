package com.pm.bankcards.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = true)
    @JoinColumn(name = "from_card_id")
    private Card fromCard;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "to_card_id")
    private Card toCard;

    @Setter
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Setter
    @Column(nullable = false, unique = true, length = 64)
    private String requestId;

    @Setter
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

}
