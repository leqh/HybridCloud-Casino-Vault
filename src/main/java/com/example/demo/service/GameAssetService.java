package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class GameAssetService {

    @Autowired
    private S3Client s3Client;

    private final String BUCKET_NAME = "casino-assets";

    public void uploadInitialAssets() {
        System.out.println("STARTING CLOUD UPLOAD");
        try {
            // 1. Tell AWS we want to upload a file named "welcome.txt"
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key("welcome.txt")
                    .build();

            // 2. Send the actual text content
            s3Client.putObject(request, RequestBody.fromString("Welcome to the Hybrid Casino!"));

            System.out.println("SUCCESS: Cloud Asset Uploaded to LocalStack S3!");
        } catch (Exception e) {
            System.err.println("ERROR: Could not upload to S3.");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("Did you create the bucket? Run: docker exec -it casino-aws awslocal s3 mb s3://casino-assets");
        }
    }
}