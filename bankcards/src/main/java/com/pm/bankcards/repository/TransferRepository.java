package com.pm.bankcards.repository;

import com.pm.bankcards.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByRequestId(String requestId);
    boolean existsByRequestId(String requestId);
}
