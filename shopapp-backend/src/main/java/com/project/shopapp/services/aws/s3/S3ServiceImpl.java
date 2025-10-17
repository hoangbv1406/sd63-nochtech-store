package com.project.shopapp.services.aws.s3;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Override
    public String uploadFile(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null) {
            ext = FilenameUtils.getExtension(original);
            if (ext != null && !ext.isBlank()) ext = "." + ext;
            else ext = "";
        }
        String key = UUID.randomUUID().toString() + ext;
        return uploadFile(file, key);
    }

    @Override
    public String uploadFile(MultipartFile file, String key) {
        try (InputStream is = file.getInputStream()) {
            long size = file.getSize();
            if (size < 0) {
                throw new IllegalArgumentException("Unknown file size; please provide sized input");
            }

            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucketName).key(key).contentType(contentType)
                    .build();

            s3Client.putObject(putReq, RequestBody.fromInputStream(is, size));
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            return url;
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file để upload lên S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String key) {
        DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
        s3Client.deleteObject(req);
    }

}