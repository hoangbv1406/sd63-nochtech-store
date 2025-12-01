package com.project.shopapp.services.affiliate;

import com.project.shopapp.dtos.AffiliateLinkDTO;
import com.project.shopapp.shared.exceptions.DataNotFoundException;
import com.project.shopapp.models.AffiliateLink;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.AffiliateLinkRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AffiliateServiceImpl implements AffiliateService {

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public AffiliateLink createLink(AffiliateLinkDTO dto, Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm"));

        var existingLink = affiliateLinkRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (existingLink.isPresent()) {
            return existingLink.get();
        }

        String trackingCode;
        do {
            trackingCode = UUID.randomUUID().toString().substring(0, 8);
        } while (affiliateLinkRepository.existsByCode(trackingCode));

        AffiliateLink newLink = AffiliateLink.builder()
                .user(user)
                .product(product)
                .code(trackingCode)
                .clicks(0)
                .build();

        return affiliateLinkRepository.save(newLink);
    }
}
