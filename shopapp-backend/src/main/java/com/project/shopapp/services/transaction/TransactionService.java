package com.project.shopapp.services.transaction;

import com.project.shopapp.models.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionsByOrderId(Long orderId);
    Transaction createTransaction(Transaction transaction);
}
