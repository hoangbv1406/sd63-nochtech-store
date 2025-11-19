package com.project.shopapp.repositories;

import com.project.shopapp.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    void deleteByCartId(Long cartId);
    CartItem findByCartIdAndProductIdAndVariantId(Long cartId, Long productId, Long variantId);
}
