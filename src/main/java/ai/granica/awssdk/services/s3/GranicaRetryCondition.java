package ai.granica.awssdk.services.s3;

import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;

import software.amazon.awssdk.core.retry.conditions.SdkRetryCondition;

public interface GranicaRetryCondition extends RetryCondition {

    @Override
    default void requestSucceeded(RetryPolicyContext context) {
        // TODO Auto-generated method stub
        RetryCondition.super.requestSucceeded(context);
    }

    @Override
    default void requestWillNotBeRetried(RetryPolicyContext context) {
        // TODO Auto-generated method stub
        RetryCondition.super.requestWillNotBeRetried(context);
    }

    @Override
    default boolean shouldRetry(RetryPolicyContext context) {
        // TODO Auto-generated method stub
        return false;
    }
    public default RetryCondition create(){
        return SdkRetryCondition.DEFAULT;
    }
}