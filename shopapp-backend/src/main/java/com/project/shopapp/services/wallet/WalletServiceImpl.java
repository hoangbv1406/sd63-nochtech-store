package com.project.shopapp.services.wallet;

import com.project.shopapp.dtos.WalletTransactionDTO;
import com.project.shopapp.enums.WalletTransactionType;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.models.Wallet;
import com.project.shopapp.models.WalletTransaction;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.repositories.WalletRepository;
import com.project.shopapp.repositories.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));
    }

    @Override
    public Wallet createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .frozenBalance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public WalletTransaction deposit(Long userId, WalletTransactionDTO dto) throws Exception {
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> createWallet(userId));

        wallet.setBalance(wallet.getBalance().add(dto.getAmount()));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(dto.getAmount())
                .type(WalletTransactionType.DEPOSIT)
                .description(dto.getDescription())
                .build();

        return walletTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) throws Exception {
        if (fromUserId.equals(toUserId)) {
            throw new Exception("Không thể tự chuyển tiền cho chính mình!");
        }

        Long firstLockId = fromUserId < toUserId ? fromUserId : toUserId;
        Long secondLockId = fromUserId < toUserId ? toUserId : fromUserId;

        Wallet firstWallet = walletRepository.findByUserIdWithLock(firstLockId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy ví người dùng ID: " + firstLockId));
        Wallet secondWallet = walletRepository.findByUserIdWithLock(secondLockId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy ví người dùng ID: " + secondLockId));

        Wallet senderWallet = fromUserId.equals(firstLockId) ? firstWallet : secondWallet;
        Wallet receiverWallet = toUserId.equals(firstLockId) ? firstWallet : secondWallet;

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new Exception("Số dư không đủ để thực hiện giao dịch!");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        WalletTransaction senderLog = WalletTransaction.builder()
                .wallet(senderWallet)
                .amount(amount)
                .type(WalletTransactionType.TRANSFER_OUT)
                .description("Chuyển tiền đến người dùng ID: " + toUserId)
                .build();

        WalletTransaction receiverLog = WalletTransaction.builder()
                .wallet(receiverWallet)
                .amount(amount)
                .type(WalletTransactionType.TRANSFER_IN)
                .description("Nhận tiền từ người dùng ID: " + fromUserId)
                .build();

        walletTransactionRepository.saveAll(Arrays.asList(senderLog, receiverLog));
    }
}
