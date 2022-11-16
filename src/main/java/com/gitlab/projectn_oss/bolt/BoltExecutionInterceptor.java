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

public class BoltExecutionInterceptor implements ExecutionInterceptor {

    // The client which we will use to make S3 requests (not Bolt)
    private S3Client s3Client = S3Client.builder().build();

    @Override
    public SdkHttpResponse modifyHttpResponse(Context.ModifyHttpResponse context,
                    ExecutionAttributes executionAttributes) {
        System.out.println("Hello from modifyHttpResponse");
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
        System.out.println("executionAttributes: " + executionAttributes());

        System.out.println("Making call to real s3Client...");
        GetObjectRequest getObjReq = (GetObjectRequest) req;
        ResponseInputStream<GetObjectResponse> respInStream = s3Client.getObject(getObjReq);
        SdkResponse response = respInStream.response();
        System.out.println("s3Client response: " + response);
        return response.sdkHttpResponse();
        // return context.httpResponse();
    }

    @Override
    public void onExecutionFailure(Context.FailedExecution context,
        ExecutionAttributes executionAttributes) {
        System.out.println("Hello from onExecutionFailure");
    }
}
