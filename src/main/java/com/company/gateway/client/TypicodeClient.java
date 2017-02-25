package com.company.gateway.client;

import com.company.gateway.dto.UserDTO;
import com.company.gateway.dto.UserPostDTO;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class TypicodeClient {

    @Value("${api.typicode}")
    private String baseUrl;

    public UserDTO getUserById(Long userId) throws UnirestException {
        final String url = String.format("%s/%s/{id}", baseUrl, "users");
        log.debug("calling '{}'", url);

        return Unirest.get(url)
                .routeParam("id", userId.toString())
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .asObject(UserDTO.class).getBody();
    }

    public List<UserPostDTO> getPostsByUserId(Long userId) throws UnirestException {
        final String url = String.format("%s/%s", baseUrl, "posts");
        log.debug("calling '{}'", url);

        return Arrays.asList(Unirest.get(url)
                .queryString("userId", userId)
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .asObject(UserPostDTO[].class).getBody());
    }
}
