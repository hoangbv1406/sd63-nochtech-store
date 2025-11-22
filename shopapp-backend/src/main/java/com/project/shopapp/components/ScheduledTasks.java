package com.project.shopapp.components;

import com.project.shopapp.enums.ProductItemStatus;
import com.project.shopapp.repositories.ProductItemRepository;
import com.project.shopapp.repositories.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final TokenRepository tokenRepository;
    private final ProductItemRepository productItemRepository;

    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        try {
            int deletedCount = tokenRepository.deleteTokensExpiredBefore(now);
            log.info("Cleaned up {} expired tokens at {}", deletedCount, now);
        } catch (Exception e) {
            log.error("Error cleaning tokens: ", e);
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void unlockProductItems() {
        LocalDateTime now = LocalDateTime.now();
        try {
            int unlockedCount = productItemRepository.unlockExpiredHoldItems(ProductItemStatus.HOLD, ProductItemStatus.AVAILABLE, now);
            if (unlockedCount > 0) {
                log.info("Successfully unlocked {} product items at {}", unlockedCount, now);
            }
        } catch (Exception e) {
            log.error("Error unlocking product items: ", e);
        }
    }
}
