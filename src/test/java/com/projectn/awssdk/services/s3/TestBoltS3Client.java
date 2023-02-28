package com.projectn.awssdk.services.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import org.junitpioneer.jupiter.SetEnvironmentVariable;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.Bucket;

public class TestBoltS3Client {

    @Test
    public void testCreate1000AWSClients() {
        Instant start = Instant.now();
        for (int i = 0; i < 10000; i++) {
            S3Client s3Client = S3Client.create();
            // Add test code here to verify the client works as expected
            // You can reuse the test code from the previous example
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time taken to create 1000 AWS clients: " + duration.toMillis() + " ms");
    }

    @Test
    // @SetEnvironmentVariable(key = "BOLT_CUSTOM_DOMAIN", value = "bolt.co")
    public void testCreate1000BoltClients() {
        Instant start = Instant.now();
        for (int i = 0; i < 10000; i++) {
            S3Client s3Client = BoltS3Client.create();
            // Add test code here to verify the client works as expected
            // You can reuse the test code from the previous example
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time taken to create 1000 Bolt clients: " + duration.toMillis() + " ms");
    }

    // Add more test methods for other S3Client methods here
}
