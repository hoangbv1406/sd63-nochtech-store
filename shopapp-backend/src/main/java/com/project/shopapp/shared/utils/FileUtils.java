package com.project.shopapp.shared.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileUtils {
    private static final String UPLOADS_FOLDER = "uploads";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return extension != null && ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }

    public static String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        Path uploadDir = Paths.get(UPLOADS_FOLDER).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = uploadDir.resolve(uniqueFilename).normalize();
        if (!destination.startsWith(uploadDir)) {
            throw new SecurityException("Security Error: Cannot store file outside of uploads directory.");
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    public static void deleteFile(String filename) throws IOException {
        Path uploadDir = Paths.get(UPLOADS_FOLDER).toAbsolutePath().normalize();
        Path filePath = uploadDir.resolve(filename).normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new SecurityException("Security Error: Cannot delete file outside of uploads directory.");
        }

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new FileNotFoundException("File not found: " + filename);
        }
    }

}
