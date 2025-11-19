package com.project.shopapp.repositories;

import com.project.shopapp.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOrderId(Long orderId);
    Optional<Transaction> findByTransactionCode(String transactionCode);
}
