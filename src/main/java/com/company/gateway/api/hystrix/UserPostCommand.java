package com.company.gateway.api.hystrix;

import com.company.gateway.client.TypicodeClient;
import com.company.gateway.dto.UserPostDTO;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserPostCommand extends BaseCommand<List<UserPostDTO>> {

    public UserPostCommand(String groupKey, int timeout, TypicodeClient typicodeClient, String debugMessage, Long userId) {
        super(groupKey, timeout, typicodeClient, debugMessage, userId);
    }

    @Override
    protected Observable<List<UserPostDTO>> construct() {
        return Observable.create(subscriber -> {
                    try {
                        subscriber.onNext(typicodeClient.getPostsByUserId(userId));
                        subscriber.onCompleted();
                    } catch (Throwable ex) {
                        log.error("Failure to get posts for userId={}", userId);
                        subscriber.onError(ex);
                    }
                }
        );
    }

    @Override
    protected Observable<List<UserPostDTO>> resumeWithFallback() {
        return Observable.create(subscriber -> {
            try {
                handleErrors();
                subscriber.onNext(new ArrayList<UserPostDTO>());
                subscriber.onCompleted();
            } catch (Exception ex) {
                log.error("Failure to get posts from fallback for userId={}", userId);
                subscriber.onError(ex);
            }
        });
    }
}
