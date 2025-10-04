package com.pm.bankcards.repository;

import com.pm.bankcards.repository.view.BalanceView;
import com.pm.bankcards.repository.view.CardListView;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.pm.bankcards.entity.Card;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    Optional<Card> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByLast4AndOwnerId(String last4, Long ownerId);

    Page<CardListView> findByOwnerId(Long ownerId,
                                    org.springframework.data.domain.Pageable pageable);

    @Query("select c.id as id, c.balance as balance from Card c where c.id = id")
    Optional<BalanceView> getBalanceById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Card> lockByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
}
