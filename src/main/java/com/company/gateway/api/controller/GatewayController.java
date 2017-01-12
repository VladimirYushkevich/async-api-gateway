package com.company.gateway.api.controller;

import com.company.gateway.api.model.UserResponse;
import com.company.gateway.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GatewayController {

    private final UserService userService;

    @RequestMapping(value = "/users/{user_id}", method = RequestMethod.GET)
    public DeferredResult<UserResponse> getUserWithPosts(@PathVariable("user_id") Long userId) {
        DeferredResult<UserResponse> deferredResult = new DeferredResult<>();
        userService.getUserWithPosts(userId).subscribe(deferredResult::setResult);
        return deferredResult;
    }

}
