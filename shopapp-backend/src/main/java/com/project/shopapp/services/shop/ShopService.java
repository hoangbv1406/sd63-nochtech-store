package com.project.shopapp.services.shop;

import com.project.shopapp.dtos.ShopDTO;
import com.project.shopapp.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShopService {
    Shop createShop(ShopDTO shopDTO, Long ownerId) throws Exception;
    Shop getShopById(Long id) throws Exception;
    Page<Shop> getActiveShops(Pageable pageable);
    Page<Shop> getAllShopsForAdmin(Pageable pageable);
}
