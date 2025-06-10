package com.ecom.payment.paymentservice.retry;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component("retryLogger")
public class RetryLogger implements RetryListener {
    private static Logger log = LoggerFactory.getLogger(RetryLogger.class);
    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if(throwable == null) {
            log.info("Operation succeeded after {} attempt(s)", context.getRetryCount() + 1);
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.warn("Retry attempt #{} failed: {}", context.getRetryCount(), throwable.getMessage());
    }
}
