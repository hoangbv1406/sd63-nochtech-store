package com.project.shopapp.services.aws.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile file);
    String uploadFile(MultipartFile file, String key);
    void deleteFile(String key);
}
