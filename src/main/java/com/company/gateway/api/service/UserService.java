package com.company.gateway.api.service;

import com.company.gateway.api.hystrix.UserCommand;
import com.company.gateway.api.hystrix.UserPostCommand;
import com.company.gateway.api.model.UserResponse;
import com.company.gateway.client.TypicodeClient;
import com.company.gateway.dto.UserDTO;
import com.company.gateway.dto.UserPostDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.inject.Inject;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class UserService {

    private final TypicodeClient typicodeClient;

    @Value("${hystrix.command.UserCommand.timeoutInMilliseconds:1000}")
    private int userTimeOut;
    @Value("${hystrix.command.UserPostCommand.timeoutInMilliseconds:1000}")
    private int postTimeOut;
    @Value("${hystrix.command.user.groupKey}")
    private String userGroupKey;

    public Observable<UserResponse> getUserWithPosts(Long userId) {
        try {
            return new UserCommand(userGroupKey, userTimeOut, typicodeClient, "getUser", userId).observe()
                    .zipWith(new UserPostCommand(userGroupKey, userTimeOut, typicodeClient, "getUserPosts", userId).observe(),
                            (UserDTO u, List<UserPostDTO> p) -> UserResponse.builder()
                                    .user(u)
                                    .posts(p)
                                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Failed to get user with id=%s", userId));
        }
    }
}
