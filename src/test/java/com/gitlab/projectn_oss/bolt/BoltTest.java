package com.gitlab.projectn_oss.bolt;

import java.nio.charset.Charset;

import javax.management.OperationsException;

// import junit.framework.Test;
import junit.framework.TestCase;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;


public class BoltTest extends TestCase
{
    // private final Aws4Signer aws4Signer;
    private S3Client boltS3Client;
    private S3Client s3Client;

    public void setUp() throws OperationsException {
        this.boltS3Client = BoltS3Client.builder().build();
        this.s3Client = S3Client.builder().build();
    }

    private int callGetObject(S3Client client, GetObjectRequest getObjectRequest){
        ResponseBytes<GetObjectResponse> response = null;
        try{
            response = client.getObject(getObjectRequest, ResponseTransformer.toBytes());
        } catch (S3Exception e){
            return e.statusCode();
        }
        System.out.println(response.asString(Charset.defaultCharset()));
        return 200;
    }

    public void testGetObject(){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("bolt-test-aqfk03")
                .key("testfile.json")
                .build();

        int statusCode = callGetObject(boltS3Client, getObjectRequest);
        if (statusCode == 404){
            statusCode = callGetObject(s3Client, getObjectRequest);
        }
        assertTrue(statusCode == 200);
    }
}
