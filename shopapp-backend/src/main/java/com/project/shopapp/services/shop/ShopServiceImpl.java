package com.project.shopapp.services.shop;

import com.project.shopapp.dtos.ShopDTO;
import com.project.shopapp.enums.ShopStatus;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Shop;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.ShopRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Shop createShop(ShopDTO shopDTO, Long ownerId) throws Exception {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng!"));

        if (shopRepository.findByOwnerId(owner.getId()).isPresent()) {
            throw new Exception("Bạn đã sở hữu một cửa hàng rồi, không thể tạo thêm!");
        }

        String baseSlug = SlugUtils.toSlug(shopDTO.getName());
        String uniqueSlug = baseSlug;
        int counter = 1;
        while (shopRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }

        Shop shop = Shop.builder()
                .name(shopDTO.getName())
                .description(shopDTO.getDescription())
                .owner(owner)
                .slug(uniqueSlug)
                .status(ShopStatus.PENDING)
                .ratingAvg(5.0f)
                .totalOrders(0)
                .build();

        return shopRepository.save(shop);
    }

    @Override
    public Shop getShopById(Long id) throws Exception {
        return shopRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy cửa hàng!"));
    }

    @Override
    public Page<Shop> getActiveShops(Pageable pageable) {
        return shopRepository.findByStatus(ShopStatus.ACTIVE, pageable);
    }

    @Override
    public Page<Shop> getAllShopsForAdmin(Pageable pageable) {
        return shopRepository.findAll(pageable);
    }
}
