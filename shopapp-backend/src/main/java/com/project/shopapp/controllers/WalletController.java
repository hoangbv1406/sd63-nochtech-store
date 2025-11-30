package com.project.shopapp.controllers;

import com.project.shopapp.dtos.WalletTransactionDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.models.Wallet;
import com.project.shopapp.models.WalletTransaction;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.wallet.WalletResponse;
import com.project.shopapp.responses.wallet.WalletTransactionResponse;
import com.project.shopapp.services.wallet.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("${api.prefix}/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> getBalance(
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            Wallet wallet = walletService.getWalletByUserId(loginUser.getId());
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Lấy thông tin ví thành công")
                    .data(WalletResponse.builder()
                            .userId(loginUser.getId())
                            .balance(wallet.getBalance())
                            .frozenBalance(wallet.getFrozenBalance())
                            .build())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> deposit(
            @Valid @RequestBody WalletTransactionDTO transactionDTO,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            WalletTransaction transaction = walletService.deposit(loginUser.getId(), transactionDTO);

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Tạo yêu cầu nạp tiền thành công")
                    .data(WalletTransactionResponse.fromTransaction(transaction))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> transfer(
            @RequestParam("to_user_id") Long toUserId,
            @RequestParam("amount") BigDecimal amount,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            walletService.transfer(loginUser.getId(), toUserId, amount);

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Chuyển tiền thành công (Tính năng đang phát triển)")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}
