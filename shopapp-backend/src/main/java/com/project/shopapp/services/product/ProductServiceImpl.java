package com.project.shopapp.services.product;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Pageable pageable) {
        Page<Product> productsPage = productRepository.searchProducts(categoryId, null, keyword, null, null, pageable);
        return productsPage.map(ProductResponse::fromProduct);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    @Override
    public Product getProductById(long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found."));
    }

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws Exception {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                () -> new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId())
        );

        Brand existingBrand = null;
        if (productDTO.getBrandId() != null) {
            existingBrand = brandRepository.findById(productDTO.getBrandId()).orElseThrow(
                    () -> new DataNotFoundException("Cannot find brand with id: " + productDTO.getBrandId()));
        }

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .brand(existingBrand)
                .specs(productDTO.getSpecs())
                .isImeiTracked(productDTO.getIsImeiTracked())
                .build();

        Product savedProduct = productRepository.save(newProduct);

        if (productDTO.getVariants() != null && !productDTO.getVariants().isEmpty()) {
            for (ProductDTO.ProductVariantDTO variantDTO : productDTO.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(savedProduct)
                        .sku(variantDTO.getSku())
                        .price(variantDTO.getPrice())
                        .originalPrice(variantDTO.getOriginalPrice())
                        .imageUrl(variantDTO.getImageUrl())
                        .build();
                productVariantRepository.save(variant);
            }
        }

        return savedProduct;
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            if (productDTO.getName() != null && !productDTO.getName().isEmpty()) {
                existingProduct.setName(productDTO.getName());
            }
            if (productDTO.getPrice() != null) {
                existingProduct.setPrice(productDTO.getPrice());
            }
            if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
                existingProduct.setDescription(productDTO.getDescription());
            }
            if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
                existingProduct.setThumbnail(productDTO.getThumbnail());
            }
            if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
                Category newCategory = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                        () -> new DataNotFoundException("Category not found")
                );
                existingProduct.setCategory(newCategory);
            }
            if (productDTO.getBrandId() != null && productDTO.getBrandId() > 0) {
                Brand newBrand = brandRepository.findById(productDTO.getBrandId()).orElseThrow(
                        () -> new DataNotFoundException("Brand not found")
                );
                existingProduct.setBrand(newBrand);
            }
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public Product deleteProduct(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found."));
        productRepository.deleteById(productId);
        return product;
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found."));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Maximum allowed images: " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        if (existingProduct.getThumbnail() == null) {
            existingProduct.setThumbnail(newProductImage.getImageUrl());
            productRepository.save(existingProduct);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public Product likeProduct(Long userId, Long productId) throws Exception {
        if (!userRepository.existsById(userId) || !productRepository.existsById(productId)) {
            throw new DataNotFoundException("User or product not found");
        }
        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            Favorite favorite = Favorite.builder()
                    .product(productRepository.findById(productId).orElse(null))
                    .user(userRepository.findById(userId).orElse(null))
                    .build();
            favoriteRepository.save(favorite);
        }
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public Product unlikeProduct(Long userId, Long productId) throws Exception {
        if (!userRepository.existsById(userId) || !productRepository.existsById(productId)) {
            throw new DataNotFoundException("User or product not found");
        }
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
        }
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public List<ProductResponse> findFavoriteProductsByUserId(Long userId) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new Exception("User not found with ID: " + userId);
        }
        List<Product> favoriteProducts = productRepository.findFavoriteProductsByUserId(userId);
        return favoriteProducts.stream().map(ProductResponse::fromProduct).collect(Collectors.toList());
    }

}
