package com.gitlab.projectn_oss.bolt;

import java.nio.charset.Charset;
// import junit.framework.Test;
import junit.framework.TestCase;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;


public class BoltTest extends TestCase
{
    // private final Aws4Signer aws4Signer;
    private S3Client s3Client;

    public void setUp() {
        this.s3Client = BoltS3Client.builder().build();
        // this.s3Client = S3Client.builder().build();
    }

    public void testGetObject(){
        // Properties props = System.getProperties();
        // props.setProperty("gate.home", "https://bolt.us-west-1.longrunningaws.bolt.projectn.co");
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("bolt-test-aqfk03")
                .key("testfile.json")
                .build();

        ResponseBytes<GetObjectResponse> response = this.s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
            System.out.println(response.asString(Charset.defaultCharset()));
            assertTrue( true );
        assertTrue( true );
    }
}
