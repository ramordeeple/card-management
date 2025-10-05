package com.pm.bankcards.repository;

import com.pm.bankcards.repository.view.CardListView;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.pm.bankcards.entity.Card;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    Optional<Card> findByIdAndOwnerId(Long id, Long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id and c.owner.id = :ownerId")
    Optional<Card> lockByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);

    Page<CardListView> findAllByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
}
