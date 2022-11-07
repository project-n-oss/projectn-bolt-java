package com.gitlab.projectn_oss.bolt;

import java.security.NoSuchAlgorithmException;
import java.util.MissingFormatArgumentException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.socket.ConnectionSocketFactory;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.internal.conn.SdkTlsSocketFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Service Client for accessing S3 via Bolt.
 * It provides the same builder as S3Client to configure and create a service client. Its endpoint always resolves to
 * Bolt Service URL as specified via the 'BOLT_URL' environment variable.
 */
public interface BoltS3Client extends S3Client {

    // Get bolt url from quicksilver. Not from the env

    static S3Client create(){
        return builder().build();
    }


    static S3ClientBuilder builder(){
        if (BoltConfig.CustomDomain == null){
            throw new MissingFormatArgumentException("BOLT_CUSTOM_DOMAIN is not set. \n"+
            "Set the environment variable to BOLT_CUSTOM_DOMAIN=my-bolt.my-domain.com. This can be obtained from `projectn ls`");  
        }
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e1) {
            System.out.println("SSL context is not initialized");
            e1.printStackTrace();
        }
        ConnectionSocketFactory socketFactory = new SdkTlsSocketFactory(sslcontext, new BoltHostnameVerifier());

        SdkHttpClient client = ApacheHttpClient.builder().socketFactory(socketFactory).build();
        
        return S3Client.builder()
                .httpClient(client)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .putAdvancedOption(SdkAdvancedClientOption.SIGNER, BoltSigner.create())
                        .build());
    }
}
