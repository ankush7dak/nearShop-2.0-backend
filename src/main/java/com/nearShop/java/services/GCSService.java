package com.nearShop.java.services;


import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GCSService { 

    // private final Storage storage;

    // @Value("${gcp.bucket.name}")
    // private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {

        // String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
        //         .setContentType(file.getContentType())
        //         .build();

        // storage.create(blobInfo, file.getBytes());

        return "https://tinyurl.com/27mzte9m";
                
    }
}