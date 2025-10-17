package com.project.shopapp.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.access:}")
    private String accessKey;

    @Value("${aws.s3.secret:}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        Region awsRegion = Region.of(region);
        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
            return S3Client.builder()
                    .region(awsRegion)
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .build();
        } else {
            return S3Client.builder()
                    .region(awsRegion)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    @Bean
    public S3Presigner s3Presigner() {
        Region awsRegion = Region.of(region);
        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
            return S3Presigner.builder()
                    .region(awsRegion)
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .build();
        } else {
            return S3Presigner.builder()
                    .region(awsRegion)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

}
