package com.bandung.ekrs.config;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.default.image}")
    private String defaultImage;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @PostConstruct
    public void init() {
        try {
            MinioClient minioClient = minioClient();
            
            // Check if bucket exists
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            // Create bucket if it doesn't exist
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Bucket '{}' created successfully", bucketName);
            }

            // Upload default image if it doesn't exist
            try {
                minioClient.statObject(StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(defaultImage)
                        .build());
                log.info("Default image already exists");
            } catch (Exception e) {
                // Default image doesn't exist, upload it
                Resource defaultImageResource = new ClassPathResource("static/images/" + defaultImage);
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(defaultImage)
                        .stream(defaultImageResource.getInputStream(), defaultImageResource.contentLength(), -1)
                        .contentType("image/jpeg")
                        .build());
                log.info("Default image '{}' uploaded successfully", defaultImage);
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO", e);
        }
    }
} 