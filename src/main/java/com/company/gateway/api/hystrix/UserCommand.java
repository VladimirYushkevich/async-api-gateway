package com.company.gateway.api.hystrix;

import com.company.gateway.client.TypicodeClient;
import com.company.gateway.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

@Slf4j
public class UserCommand extends BaseCommand<UserDTO> {

    public UserCommand(String groupKey, int timeout, TypicodeClient typicodeClient, String debugMessage, Long userId) {
        super(groupKey, timeout, typicodeClient, debugMessage, userId);
    }

    @Override
    protected Observable<UserDTO> construct() {
        return Observable.create(subscriber -> {
                    try {
                        subscriber.onNext(typicodeClient.getUserById(userId));
                        subscriber.onCompleted();
                    } catch (Throwable ex) {
                        log.error("Failure to get user with id {}", userId);
                        subscriber.onError(ex);
                    }
                }
        );
    }
}
