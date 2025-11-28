package com.project.shopapp.repositories;

import com.project.shopapp.enums.WalletTransactionType;
import com.project.shopapp.models.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    Page<WalletTransaction> findByWalletIdAndTypeOrderByCreatedAtDesc(Long walletId, WalletTransactionType type, Pageable pageable);

    Optional<WalletTransaction> findByRefOrderId(Long refOrderId);

}
