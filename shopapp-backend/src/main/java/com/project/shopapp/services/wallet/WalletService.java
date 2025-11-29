package com.project.shopapp.services.wallet;

import com.project.shopapp.dtos.WalletTransactionDTO;
import com.project.shopapp.models.Wallet;
import com.project.shopapp.models.WalletTransaction;

import java.math.BigDecimal;

public interface WalletService {
    Wallet getWalletByUserId(Long userId);
    Wallet createWallet(Long userId);
    WalletTransaction deposit(Long userId, WalletTransactionDTO transactionDTO) throws Exception;
    void transfer(Long fromUserId, Long toUserId, BigDecimal amount) throws Exception;
}
