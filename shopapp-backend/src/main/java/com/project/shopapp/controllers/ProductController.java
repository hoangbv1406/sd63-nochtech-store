package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.dtos.ProductVariantDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.models.ProductVariant;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.product.ProductListResponse;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.services.aws.s3.S3Service;
import com.project.shopapp.services.product.ProductService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final LocalizationUtils localizationUtils;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0", name = "brand_id") Long brandId,
            @RequestParam(defaultValue = "0", name = "shop_id") Long shopId,
            @RequestParam(required = false, name = "min_price") BigDecimal minPrice,
            @RequestParam(required = false, name = "max_price") BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, limit, sort);

        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, brandId, shopId, minPrice, maxPrice, pageRequest);

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy danh sách sản phẩm thành công")
                .status(HttpStatus.OK)
                .data(ProductListResponse.builder()
                        .products(productPage.getContent())
                        .totalPages(productPage.getTotalPages())
                        .build())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getProductById(@PathVariable("id") Long productId) {
        Product existingProduct = productService.getProductById(productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ProductResponse.fromProduct(existingProduct))
                .message("Lấy chi tiết sản phẩm thành công")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result,
            @AuthenticationPrincipal User loginUser
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(String.join("; ", errorMessages))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

        Product newProduct = productService.createProduct(productDTO, loginUser.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Tạo sản phẩm thành công")
                .status(HttpStatus.CREATED)
                .data(ProductResponse.fromProduct(newProduct))
                .build());
    }

    @PostMapping("/{id}/variants")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> addVariantToProduct(
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductVariantDTO variantDTO,
            @AuthenticationPrincipal User loginUser
    ) throws Exception {
        ProductVariant variant = productService.addVariantToProduct(productId, variantDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Thêm biến thể sản phẩm thành công")
                .status(HttpStatus.CREATED)
                .data(variant)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO,
            @AuthenticationPrincipal User loginUser
    ) throws Exception {
        Product updatedProduct = productService.updateProduct(id, productDTO, loginUser.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ProductResponse.fromProduct(updatedProduct))
                .message("Cập nhật sản phẩm thành công")
                .status(HttpStatus.OK)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Xóa sản phẩm thành công")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VENDOR')")
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<MultipartFile>() : files;

        if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Bạn chỉ được upload tối đa " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT + " ảnh")
                    .build());
        }

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getSize() == 0) continue;

            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(ResponseObject.builder()
                        .message("Kích thước file không được vượt quá 10MB")
                        .status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .build());
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ResponseObject.builder()
                        .message("Chỉ chấp nhận file ảnh")
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .build());
            }

            String uploaded = s3Service.uploadFile(file);
            String imageUrl = uploaded.startsWith("http") ? uploaded
                    : String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uploaded);

            try {
                String originalFilename = file.getOriginalFilename() == null ? "file-" + System.currentTimeMillis() : file.getOriginalFilename();
                String localFilename = uploaded;
                if (localFilename.contains("/")) {
                    localFilename = localFilename.substring(localFilename.lastIndexOf("/") + 1);
                }

                Path uploadsDir = Paths.get("uploads");
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                }
                Path target = uploadsDir.resolve(localFilename);

                try (java.io.InputStream in = file.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                System.err.println("Lỗi lưu file local (có thể bỏ qua): " + e.getMessage());
            }

            ProductImageDTO dto = ProductImageDTO.builder().imageUrl(imageUrl).build();
            ProductImage productImage = productService.createProductImage(existingProduct.getId(), dto);
            productImages.add(productImage);
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Upload ảnh thành công")
                .status(HttpStatus.CREATED)
                .data(productImages)
                .build());
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable("imageName") String imageName) {
        try {
            Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/like/{productId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> likeProduct(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal User loginUser
    ) throws Exception {
        Product likedProduct = productService.likeProduct(loginUser.getId(), productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ProductResponse.fromProduct(likedProduct))
                .message("Đã thích sản phẩm")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/unlike/{productId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> unlikeProduct(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal User loginUser
    ) throws Exception {
        Product unlikedProduct = productService.unlikeProduct(loginUser.getId(), productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ProductResponse.fromProduct(unlikedProduct))
                .message("Đã bỏ thích sản phẩm")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/favorite-products")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getFavoriteProducts(@AuthenticationPrincipal User loginUser) throws Exception {
        List<ProductResponse> favoriteProducts = productService.findFavoriteProductsByUserId(loginUser.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .data(favoriteProducts)
                .message("Lấy danh sách sản phẩm yêu thích thành công")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/generateFakeLikes")
    public ResponseEntity<String> generateFakeLikes() {
        return ResponseEntity.ok("Fake likes generated successfully.");
    }

}
