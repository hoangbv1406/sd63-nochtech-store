package com.project.shopapp.shared.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String storeFile(MultipartFile file) throws IOException;
    void deleteFile(String fileName) throws IOException;
    String getFileUrl(String fileName);
}
