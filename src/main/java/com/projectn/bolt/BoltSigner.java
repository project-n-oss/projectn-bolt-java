package com.projectn.bolt;

import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.query.AwsQueryProtocolFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.transform.GetCallerIdentityRequestMarshaller;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BoltSigner implements Signer {

    private static final String StsEndpoint = "https://sts.amazonaws.com/";
    public static final String awsSTSRequestBody = "Action=GetCallerIdentity&Version=2011-06-15";
    private final Aws4Signer aws4Signer;

    private BoltSigner() {
        aws4Signer = Aws4Signer.create();
    }

    public static BoltSigner create() {
        return new BoltSigner();
    }

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {

        Aws4SignerParams iamSignerParams = Aws4SignerParams.builder().
                awsCredentials(executionAttributes.getAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS))
                .signingName("sts")
                .signingRegion(Region.US_EAST_1)
                .build();

        return sign(request, iamSignerParams);
    }

    public SdkHttpFullRequest sign(SdkHttpFullRequest request, Aws4SignerParams signingParams) {
        SdkHttpFullRequest iamRequest = new GetCallerIdentityRequestMarshaller(AwsQueryProtocolFactory
                .builder().clientConfiguration(SdkClientConfiguration.builder()
                        .option(SdkClientOption.ENDPOINT, URI.create(StsEndpoint))
                        .build())
                .build())
                .marshall(GetCallerIdentityRequest.builder().build());

        SdkHttpFullRequest.Builder iamRequestBuilder = iamRequest.toBuilder().clearQueryParameters()
                .contentStreamProvider(() -> new ByteArrayInputStream( awsSTSRequestBody.getBytes(StandardCharsets.UTF_8)));

        Aws4SignerParams iamSignerParams = Aws4SignerParams.builder().
                awsCredentials(signingParams.awsCredentials())
                .signingName("sts")
                .signingRegion(Region.US_EAST_1)
                .build();

        SdkHttpFullRequest signedIamRequest = aws4Signer.sign(iamRequestBuilder.build(),
                iamSignerParams);

        SdkHttpFullRequest.Builder signedRequestBuilder = request.toBuilder();
        Map<String, List<String>> iamHeaders = signedIamRequest.headers();


        signedRequestBuilder.putHeader("X-Amz-Security-Token", iamHeaders.get("X-Amz-Security-Token"));
        signedRequestBuilder.putHeader("X-Amz-Date", iamHeaders.get("X-Amz-Date"));
        signedRequestBuilder.putHeader("Authorization", iamHeaders.get("Authorization"));

        return signedRequestBuilder.build();
    }
}
