package com.project.shopapp.services.product;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Pageable pageable);
    List<Product> findProductsByIds(List<Long> productIds);
    Product getProductById(long id);
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    Product deleteProduct(long id);
    Product likeProduct(Long userId, Long productId) throws Exception;
    Product unlikeProduct(Long userId, Long productId) throws Exception;
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;
    List<ProductResponse> findFavoriteProductsByUserId(Long userId) throws Exception;
}
