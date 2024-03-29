package ai.granica.awssdk.services.s3;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullRequest.Builder;
import software.amazon.awssdk.http.SdkHttpMethod;

// import com.amazonaws.util.Base64;

/**
 * BoltSigner is the AWS4 protocol signer for Bolt. It uses the credentials from the incoming S3 request and
 * canonically signs the request as a STS GetCallerIdentity API call.
 */

public class GranicaSigner implements Signer {
    private final Aws4Signer aws4Signer;

    private GranicaSigner() {
        aws4Signer = Aws4Signer.create();
    }

    public static GranicaSigner create() {
        return new GranicaSigner();
    }

    /**
     * Method that takes in a Request and returns the signed version of the request that has been canonically signed as
     * a STS GetCallerIdentity API call.
     * @param request The request to sign
     * @param executionAttributes attributes (e.g credentials) required for signing the request
     * @return signed input request
     */
    public SdkHttpFullRequest sign(SdkHttpFullRequest request, ExecutionAttributes executionAttributes) {
        URI boltURI = GranicaConfig.selectBoltEndpoints(request.method().name());
        String sourceBucket = request.getUri().getRawPath().split("/").length > 1? 
                    request.getUri().getRawPath().split("/")[1] : "n-auth-dummy";
        if (GranicaConfig.AuthBucket != null){
            sourceBucket = GranicaConfig.AuthBucket;
        }
        String prefix = getRandomString();
        String headObjectURL = String.format("https://s3.%s.amazonaws.com/%s/%s/auth", GranicaConfig.Region, sourceBucket, prefix);

        Builder requestBuilder = request.toBuilder();
        request = requestBuilder.uri(boltURI).build();

        SdkHttpFullRequest headRequest = SdkHttpFullRequest
                .builder()
                .method(SdkHttpMethod.HEAD)
                .uri(URI.create(headObjectURL))
                //SHA value for empty payload. As head object request is with empty payload
                .appendHeader("X-Amz-Content-Sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                .build();

        SdkHttpFullRequest signedIamRequest = aws4Signer.sign(headRequest, executionAttributes);
        SdkHttpFullRequest.Builder signedRequestBuilder = request.toBuilder();
        Map<String, List<String>> iamHeaders = signedIamRequest.headers();

        if (iamHeaders.containsKey("X-Amz-Security-Token")) {
            signedRequestBuilder.putHeader("X-Amz-Security-Token", iamHeaders.get("X-Amz-Security-Token"));
        }

        if (iamHeaders.containsKey("X-Amz-Date")) {
            signedRequestBuilder.putHeader("X-Amz-Date", iamHeaders.get("X-Amz-Date"));
        }

        if (iamHeaders.containsKey("Authorization")) {
            signedRequestBuilder.putHeader("Authorization", iamHeaders.get("Authorization"));
        }
        if (iamHeaders.containsKey("x-amz-content-sha256")) {
            signedRequestBuilder.putHeader("x-amz-content-sha256", iamHeaders.get("x-amz-content-sha256"));
        }

        signedRequestBuilder.putHeader("Host", GranicaConfig.BoltHostname);
        signedRequestBuilder.putHeader("X-Bolt-Auth-Prefix", prefix);
        signedRequestBuilder.putHeader("User-Agent", String.format("%s%s", GranicaConfig.UserAgentPrefix, request.headers().get("User-Agent")));

        request = signedRequestBuilder.build();
        return request;
    }
    private static String getRandomString(){
        String chars = "qwertyuiopasdfghjklzxcvbnm";
        return RandomStringUtils.random(4, chars);
    }

}
