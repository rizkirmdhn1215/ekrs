package com.bandung.ekrs.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.default.image:sukvi}")
    private String defaultImageName;

    @Value("${minio.endpoint}")
    private String endpoint;

    public String uploadFile(MultipartFile file, String fileName, String folder) {
        try {
            ensureBucketExists();

            String objectName = folder + "/" + fileName;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return getFileUrl(objectName);
        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage());
            throw new RuntimeException("Could not upload file to MinIO", e);
        }
    }

    public String getFileUrl(String objectName) {
        try {
            if (!objectExists(objectName)) {
                throw new RuntimeException("File not found: " + objectName);
            }

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            log.error("Error generating file URL: {}", e.getMessage());
            throw new RuntimeException("Could not generate file URL", e);
        }
    }

    public InputStream getFile(String objectName) {
        try {
            if (!objectExists(objectName)) {
                throw new RuntimeException("File not found: " + objectName);
            }

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error getting file from MinIO: {}", e.getMessage());
            throw new RuntimeException("Could not get file from MinIO", e);
        }
    }

    public void deleteFile(String objectName) {
        try {
            if (objectExists(objectName)) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
            }
        } catch (Exception e) {
            log.error("Error deleting file from MinIO: {}", e.getMessage());
            throw new RuntimeException("Could not delete file from MinIO", e);
        }
    }

    private boolean objectExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception e) {
            log.error("Error checking/creating bucket: {}", e.getMessage());
            throw new RuntimeException("Could not ensure bucket exists", e);
        }
    }

    public String uploadImage(MultipartFile file, String fileName) {
        try {
            ensureBucketExists();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return getPresignedUrl(fileName);
        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("Could not upload file to MinIO", e);
        }
    }

    public InputStream getImage(String fileName) {
        try {
            if (!objectExists(fileName)) {
                fileName = defaultImageName;
            }

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error getting image from MinIO", e);
            try {
                return minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(defaultImageName)
                                .build()
                );
            } catch (Exception ex) {
                log.error("Error getting default image from MinIO", ex);
                throw new RuntimeException("Could not get image from MinIO", e);
            }
        }
    }

    public String getImageUrl(String fileName) {
        return String.format("%s/%s/%s", endpoint, bucketName, fileName);
    }

    public void deleteImage(String fileName) {
        try {
            if (!fileName.equals(defaultImageName) && objectExists(fileName)) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
            }
        } catch (Exception e) {
            log.error("Error deleting file from MinIO", e);
            throw new RuntimeException("Could not delete file from MinIO", e);
        }
    }

    private String getPresignedUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            log.error("Error generating presigned URL", e);
            throw new RuntimeException("Could not generate presigned URL", e);
        }
    }
}