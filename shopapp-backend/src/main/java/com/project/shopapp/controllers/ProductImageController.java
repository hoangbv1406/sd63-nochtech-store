package com.project.shopapp.controllers;

import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.aws.s3.S3Service;
import com.project.shopapp.services.product.ProductService;
import com.project.shopapp.services.product.image.ProductImageService;
import com.project.shopapp.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/product-images")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;
    private final ProductService productService;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private String extractS3KeyFromUrl(String imageUrl, String bucketName) {
        if (imageUrl == null || imageUrl.isBlank()) return imageUrl;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        try {
            java.net.URI uri = new java.net.URI(imageUrl);
            String path = uri.getPath();
            if (path == null) return imageUrl;
            if (path.startsWith("/")) path = path.substring(1);
            if (path.startsWith(bucketName + "/")) return path.substring(bucketName.length() + 1);
            return path;
        } catch (Exception e) {
            int lastSlash = imageUrl.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash + 1 < imageUrl.length()) return imageUrl.substring(lastSlash + 1);
            return imageUrl;
        }
    }

    @DeleteMapping("/{productImageId}")
    public ResponseEntity<ResponseObject> deleteProductImage(@PathVariable("productImageId") Long productImageId) throws Exception {
        ProductImage productImage = productImageService.deleteProductImage(productImageId);

        if (productImage != null) {
            String imageUrl = productImage.getImageUrl();
            if (imageUrl != null && !imageUrl.isBlank()) {
                String key = extractS3KeyFromUrl(imageUrl, bucketName);
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    try {
                        s3Service.deleteFile(key);
                    } catch (Exception e) {
                        System.err.println("Failed to delete file from S3 (key=" + key + "): " + e.getMessage());
                    }
                }
                try {
                    String localFilename = java.nio.file.Paths.get(key.replace("\\", "/")).getFileName().toString();
                    java.nio.file.Path localPath = java.nio.file.Paths.get("uploads").resolve(localFilename);
                    java.nio.file.Files.deleteIfExists(localPath);
                } catch (Exception e) {
                    System.err.println("Failed to delete local file for key=" + key + " : " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Product image deleted successfully. productImageId = " + productImageId)
                .data(productImage)
                .status(HttpStatus.OK)
                .build()
        );
    }

}
