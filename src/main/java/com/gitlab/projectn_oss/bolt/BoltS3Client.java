package com.gitlab.projectn_oss.bolt;

import javax.net.ssl.SSLContext;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.conn.socket.ConnectionSocketFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.internal.conn.SdkTlsSocketFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

// import java.net.URI;


/**
 * Service Client for accessing S3 via Bolt.
 * It provides the same builder as S3Client to configure and create a service client. Its endpoint always resolves to
 * Bolt Service URL as specified via the 'BOLT_URL' environment variable.
 */
public interface BoltS3Client extends S3Client {

    // Get bolt url from quicksilver. Not from the env

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
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e1) {
            System.out.println("SSL context is not initialized with error");
            e1.printStackTrace();
        }
        try {
            sslcontext.init(null, null, null);
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ConnectionSocketFactory socketFactory = new SdkTlsSocketFactory(sslcontext, new BoltHostnameVerifier());

        //TODO return error if boltconfig.CustomDomain is not set
        return S3Client.builder()
                .httpClient(ApacheHttpClient.builder().socketFactory(socketFactory).build())
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .putAdvancedOption(SdkAdvancedClientOption.SIGNER, BoltSigner.create())
                        .putHeader("X-Bolt-Passthrough-Read", "disable")
                        .addExecutionInterceptor(new BoltExecutionInterceptor())
                        .build());
    }
}
