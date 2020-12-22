package com.projectn.bolt;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Service Client for accessing S3 via Bolt.
 * It provides the same builder as S3Client to configure and create a service client. Its endpoint always resolves to
 * Bolt Service URL as specified via the 'BOLT_URL' environment variable.
 */
public interface BoltS3Client extends S3Client {

    String BoltServiceUrl = System.getenv("BOLT_URL").replace("{region}", Region());

    /**
     * Creates a S3Client with the credentials loaded from the application's default configuration.
     * @return S3Client
     */
    static S3Client create() {
        return builder().build();
    }

    /**
     * Constructs a builder that can be used to configure and create a S3Client.
     * @return S3ClientBuilder
     */
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

    static String Region() {
        String region = System.getenv("AWS_REGION");
        if (region != null) {
            return region;
        } else {
            return EC2MetadataUtils.getEC2InstanceRegion();
        }
    }
}
