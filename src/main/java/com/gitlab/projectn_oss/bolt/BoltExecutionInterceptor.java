package com.gitlab.projectn_oss.bolt;

import software.amazon.awssdk.core.interceptor.*;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.ResponseInputStream;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;

public class BoltExecutionInterceptor implements ExecutionInterceptor {

    // The client which we will use to make requests to S3 (not Bolt)
    private S3Client s3Client = S3Client.builder().build();

    @Override
    public SdkHttpResponse modifyHttpResponse(Context.ModifyHttpResponse context,
                    ExecutionAttributes executionAttributes) {
        System.out.println("BoltExecutionInterceptor: Hello from modifyHttpResponse");

        // Print out all executionAttributes
        Map<ExecutionAttribute<?>, Object> allAttrs = executionAttributes.getAttributes();
        for (ExecutionAttribute ea: allAttrs.keySet() ) {
            System.out.println("  " + ea + "->" + allAttrs.get(ea));
        }

//        return context.httpResponse();

        SdkRequest req = context.request();
        if (!(req instanceof GetObjectRequest)) {
            System.out.println("request is NOT GetObject, do nothing");
            return context.httpResponse();
        }

        System.out.println("request is GetObject");
        System.out.println("  req: "+ req);

        System.out.println("httpResponse: " + context.httpResponse());
        System.out.println("  statusCode: " + context.httpResponse().statusCode());
        System.out.println("  statusText: " + context.httpResponse().statusText());

        System.out.println("Making call to real s3Client...");
        GetObjectRequest getObjReq = (GetObjectRequest) req;
        ResponseInputStream<GetObjectResponse> respInStream = s3Client.getObject(getObjReq);

        BufferedReader reader = new BufferedReader(new InputStreamReader(respInStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println("response-line: " + line);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }

        SdkHttpResponse response = respInStream.response().sdkHttpResponse();
        
        System.out.println("from real s3Client, httpResponse: " + response);
        System.out.println("  statusCode: " + response.statusCode());
        System.out.println("  statusText: " + response.statusText());

        return response;
    }
}
