package com.project.shopapp.services.product;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.dtos.ProductVariantDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.shared.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;
    private final VariantValueRepository variantValueRepository;

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Long brandId, Long shopId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> productsPage = productRepository.searchProducts(keyword, categoryId, brandId, shopId, minPrice, maxPrice, pageable);
        return productsPage.map(ProductResponse::fromProduct);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    @Override
    public Product getProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO, Long shopId) throws Exception {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục"));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy cửa hàng"));

        Brand brand = null;
        if (productDTO.getBrandId() != null) {
            brand = brandRepository.findById(productDTO.getBrandId())
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thương hiệu"));
        }

        String baseSlug = SlugUtils.toSlug(productDTO.getName());
        String uniqueSlug = baseSlug;
        int counter = 1;
        while (productRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(category)
                .brand(brand)
                .shop(shop)
                .slug(uniqueSlug)
                .specs(productDTO.getSpecs())
                .isImeiTracked(productDTO.getIsImeiTracked())
                .productType(productDTO.getProductType())
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    @Transactional
    public ProductVariant addVariantToProduct(Long productId, ProductVariantDTO variantDTO) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm cha với ID: " + productId));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(variantDTO.getSku())
                .price(variantDTO.getPrice())
                .originalPrice(variantDTO.getOriginalPrice())
                .imageUrl(variantDTO.getImageUrl())
                .quantity(variantDTO.getQuantity())
                .weight(variantDTO.getWeight())
                .dimensions(variantDTO.getDimensions())
                .build();

        ProductVariant savedVariant = productVariantRepository.save(variant);

        if (variantDTO.getOptionValueIds() != null && !variantDTO.getOptionValueIds().isEmpty()) {
            for (Long optionValueId : variantDTO.getOptionValueIds()) {

                OptionValue optionValue = optionValueRepository.findById(optionValueId)
                        .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Giá trị thuộc tính với ID: " + optionValueId));

                VariantValue variantValue = VariantValue.builder()
                        .variant(savedVariant)
                        .product(product)
                        .option(optionValue.getOption())
                        .optionValue(optionValue)
                        .build();

                variantValueRepository.save(variantValue);
            }
        }

        return savedVariant;
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO, Long shopId) throws Exception {
        Product existingProduct = getProductById(id);

        if (!existingProduct.getShop().getId().equals(shopId)) {
            throw new Exception("Bạn không có quyền chỉnh sửa sản phẩm này!");
        }

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
            Category newCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy danh mục"));
            existingProduct.setCategory(newCategory);
        }
        if (productDTO.getBrandId() != null && productDTO.getBrandId() > 0) {
            Brand newBrand = brandRepository.findById(productDTO.getBrandId())
                    .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thương hiệu"));
            existingProduct.setBrand(newBrand);
        }
        if (productDTO.getSpecs() != null) {
            existingProduct.setSpecs(productDTO.getSpecs());
        }
        if (productDTO.getProductType() != null) {
            existingProduct.setProductType(productDTO.getProductType());
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public Product deleteProduct(long productId) {
        Product product = getProductById(productId);
        product.setDeletedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = getProductById(productId);

        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Đã đạt giới hạn số lượng ảnh tối đa: " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        if (existingProduct.getThumbnail() == null || existingProduct.getThumbnail().isEmpty()) {
            existingProduct.setThumbnail(newProductImage.getImageUrl());
            productRepository.save(existingProduct);
        }

        return productImageRepository.save(newProductImage);
    }

    @Override
    @Transactional
    public Product likeProduct(Long userId, Long productId) throws Exception {
        if (!userRepository.existsById(userId) || !productRepository.existsById(productId)) {
            throw new DataNotFoundException("Không tìm thấy người dùng hoặc sản phẩm");
        }
        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            Favorite favorite = Favorite.builder()
                    .product(productRepository.findById(productId).orElse(null))
                    .user(userRepository.findById(userId).orElse(null))
                    .build();
            favoriteRepository.save(favorite);
        }
        return getProductById(productId);
    }

    @Override
    @Transactional
    public Product unlikeProduct(Long userId, Long productId) throws Exception {
        if (!userRepository.existsById(userId) || !productRepository.existsById(productId)) {
            throw new DataNotFoundException("Không tìm thấy người dùng hoặc sản phẩm");
        }
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
        }
        return getProductById(productId);
    }

    @Override
    public List<ProductResponse> findFavoriteProductsByUserId(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new Exception("Không tìm thấy người dùng với ID: " + userId);
        }
        List<Product> favoriteProducts = productRepository.findFavoriteProductsByUserId(userId);
        return favoriteProducts.stream().map(ProductResponse::fromProduct).collect(Collectors.toList());
    }

}
