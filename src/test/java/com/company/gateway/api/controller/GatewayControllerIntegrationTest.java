package com.company.gateway.api.controller;

import com.company.gateway.client.TypicodeClient;
import com.company.gateway.dto.UserDTO;
import com.company.gateway.dto.UserPostDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayControllerIntegrationTest {

    @Autowired
    private GatewayController gatewayController;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TypicodeClient typicodeClient;

    @Value("http://localhost:${local.server.port}")
    private String base;

    @Before
    public void setUp() throws Exception {
        when(typicodeClient.getUserById(1L)).thenReturn(objectMapper.readValue(getClass().getResourceAsStream("/json/user.json"),
                UserDTO.class));
        when(typicodeClient.getPostsByUserId(1L)).thenReturn(objectMapper.readValue(getClass().getResourceAsStream("/json/userPosts.json"),
                new TypeReference<List<UserPostDTO>>() {
                }));
    }

    @Test
    public void testGetUserWithPosts_success() throws Exception {
        RestAssured.when()
            .get(base + "/gateway/users/{user_id}", 1L).prettyPeek()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("user.id", is(1))
            .body("posts.size()", is(10));
    }

    @Test
    public void testGetUserWithPosts_postsTimeOut() throws Exception {
        doAnswer(invocation -> {
            Thread.sleep(2000);
            return null;
        }).when(typicodeClient).getPostsByUserId(any());

        RestAssured.when()
            .get(base + "/gateway/users/{user_id}", 1L).prettyPeek()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("user.id", is(1))
            .body("posts.isEmpty()", is(true));
    }

    @Test
    public void testGetUserWithPosts_postsFail() throws Exception {
        when(typicodeClient.getPostsByUserId(any())).thenThrow(new RuntimeException("Post command failed"));

        RestAssured.when()
            .get(base + "/gateway/users/{user_id}", 1L).prettyPeek()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("user.id", is(1))
            .body("posts.isEmpty()", is(true));
    }

    @Test
    public void testGetUserWithPosts_userFail() throws Exception {
        when(typicodeClient.getUserById(any())).thenThrow(new RuntimeException("UserResponse command failed"));

        RestAssured.when()
            .get(base + "/gateway/users/{user_id}", 1L).prettyPeek()
        .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
