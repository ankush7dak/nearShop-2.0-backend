package com.nearShop.java.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2Service {

    private final S3Client s3Client;

    private final String bucketName = "nearshopimages";

    public String uploadFile(MultipartFile file) {
        try{
        String key = "products/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return "https://pub-22d0a364b7b44f3a9521eed1c0ec7b5e.r2.dev"
                + "/" + key;

    } catch (IOException e) {
        throw new RuntimeException("File upload failed", e);
    }
    }
}