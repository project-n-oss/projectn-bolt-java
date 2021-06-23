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

/**
 * BoltSigner is the AWS4 protocol signer for Bolt. It uses the credentials from the incoming S3 request and
 * canonically signs the request as a STS GetCallerIdentity API call.
 */
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

    /**
     * Method that takes in a Request and returns the signed version of the request that has been canonically signed as
     * a STS GetCallerIdentity API call.
     * @param request The request to sign
     * @param executionAttributes attributes (e.g credentials) required for signing the request
     * @return signed input request
     */
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {

        Aws4SignerParams iamSignerParams = Aws4SignerParams.builder().
                awsCredentials(executionAttributes.getAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS))
                .signingName("sts")
                .signingRegion(Region.US_EAST_1)
                .build();

        return sign(request, iamSignerParams);
    }

    /**
     * Method computes and signs the specified request, using AWS4 signing protocol, as a STS GetCallerIdentity API call.
     * The method uses credentials from the incoming request to compute the signature and adds it to the 'Authorization'
     * request header.
     * @param request The request to sign
     * @param signingParams parameters required for signing the request
     * @return signed input request
     */
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, Aws4SignerParams signingParams) {

        // Construct a STS GetCallerIdentity Request
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

        // Add request headers that are used to compute signature. Bolt will forward these request headers to the STS
        // GetCallerIdentity API

        if (iamHeaders.containsKey("X-Amz-Security-Token")) {
            signedRequestBuilder.putHeader("X-Amz-Security-Token", iamHeaders.get("X-Amz-Security-Token"));
        }

        if (iamHeaders.containsKey("X-Amz-Date")) {
            signedRequestBuilder.putHeader("X-Amz-Date", iamHeaders.get("X-Amz-Date"));
        }

        if (iamHeaders.containsKey("Authorization")) {
            signedRequestBuilder.putHeader("Authorization", iamHeaders.get("Authorization"));
        }

        return signedRequestBuilder.build();
    }
}
