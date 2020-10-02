package com.projectn.bolt;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

public interface BoltS3Client extends S3Client {

    String BoltServiceUrl = System.getenv("BOLT_URL");

    static S3Client create() {
        return builder().build();
    }

    static S3ClientBuilder builder() {
        return S3Client.builder()
                .endpointOverride(URI.create(BoltServiceUrl))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .putAdvancedOption(SdkAdvancedClientOption.SIGNER, BoltSigner.create())
                        .build());
    }
}
