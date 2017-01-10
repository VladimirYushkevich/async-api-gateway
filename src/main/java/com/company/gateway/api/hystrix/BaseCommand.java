package com.company.gateway.api.hystrix;

import com.company.gateway.client.TypicodeClient;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseCommand<R> extends HystrixObservableCommand<R> {

    private final String debugMessage;

    protected final TypicodeClient typicodeClient;
    protected final Long userId;

    protected BaseCommand(String groupKey, int timeout, TypicodeClient typicodeClient, String debugMessage, Long userId) {
        super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(timeout))
        );
        this.typicodeClient = typicodeClient;
        this.debugMessage = debugMessage;
        this.userId = userId;
    }

    protected void handleErrors() {
        final String message;
        if (isFailedExecution()) {
            message = getMessagePrefix() + "FAILED: " + getFailedExecutionException().getMessage();
        } else if (isResponseTimedOut()) {
            message = getMessagePrefix() + "TIMED OUT";
        } else {
            message = getMessagePrefix() + "SOME OTHER FAILURE";
        }
        log.warn(message);
    }

    private String getMessagePrefix() {
        return this.getClass().getSimpleName() + " [" + debugMessage + "]: ";
    }
}
