package com.company.gateway.api.hystrix;

import com.company.gateway.api.model.UserResponse;
import com.company.gateway.client.TypicodeClient;
import com.company.gateway.dto.UserDTO;
import com.company.gateway.dto.UserPostDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserResponseHystrixCommandTest {

    @Mock
    private TypicodeClient typicodeClient;

    private UserCommand userCommand;
    private UserPostCommand userPostCommand;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        when(typicodeClient.getUserById(1L)).thenReturn(objectMapper.readValue(getClass().getResourceAsStream("/json/user.json"),
                UserDTO.class));
        when(typicodeClient.getPostsByUserId(1L)).thenReturn(objectMapper.readValue(getClass().getResourceAsStream("/json/userPosts.json"),
                new TypeReference<List<UserPostDTO>>() {
                }));

        userCommand = new UserCommand("Tests", 1000, typicodeClient, "getUser", 1L);
        userPostCommand = new UserPostCommand("Tests", 1000, typicodeClient, "getUserPosts", 1L);
    }

    @Test
    public void testGetUserWithPosts_success() throws Exception {
        UserResponse userResponse = userCommand.observe()
                .zipWith(userPostCommand.observe(),
                        (UserDTO u, List<UserPostDTO> p) -> UserResponse.builder()
                                .user(u)
                                .posts(p)
                                .build())
                .toBlocking().toFuture().get();

        assertThat(userResponse.getUser().getId(), is(1L));
        assertThat(userResponse.getUser().getName(), is("Leanne Graham"));
        assertThat(userResponse.getUser().getAddress().getStreet(), is("Kulas Light"));
        assertThat(userResponse.getUser().getAddress().getGeo().getLat(), is(-37.3159f));
        assertThat(userResponse.getUser().getCompany().getName(), is("Romaguera-Crona"));
        assertThat(userResponse.getPosts().size(), is(10));
    }

    @Test
    public void testGetUserWithPosts_postsTimeOut() throws Exception {
        doAnswer(invocation -> {
            Thread.sleep(2000);
            return null;
        }).when(typicodeClient).getPostsByUserId(any());

        UserResponse userResponse = userCommand.observe()
                .zipWith(userPostCommand.observe(),
                        (UserDTO u, List<UserPostDTO> p) -> UserResponse.builder()
                                .user(u)
                                .posts(p)
                                .build())
                .toBlocking().toFuture().get();

        assertThat(userResponse.getUser().getId(), is(1L));
        assertThat(userResponse.getUser().getName(), is("Leanne Graham"));
        assertThat(userResponse.getUser().getAddress().getStreet(), is("Kulas Light"));
        assertThat(userResponse.getUser().getAddress().getGeo().getLat(), is(-37.3159f));
        assertThat(userResponse.getUser().getCompany().getName(), is("Romaguera-Crona"));
        assertThat(userResponse.getPosts().size(), is(0));
    }

    @Test
    public void testGetUserWithPosts_postsFail() throws Exception {
        when(typicodeClient.getPostsByUserId(any())).thenThrow(new RuntimeException("Post command failed"));

        UserResponse userResponse = userCommand.observe()
                .zipWith(userPostCommand.observe(),
                        (UserDTO u, List<UserPostDTO> p) -> UserResponse.builder()
                                .user(u)
                                .posts(p)
                                .build())
                .toBlocking().toFuture().get();

        assertThat(userResponse.getUser().getId(), is(1L));
        assertThat(userResponse.getUser().getName(), is("Leanne Graham"));
        assertThat(userResponse.getUser().getAddress().getStreet(), is("Kulas Light"));
        assertThat(userResponse.getUser().getAddress().getGeo().getLat(), is(-37.3159f));
        assertThat(userResponse.getUser().getCompany().getName(), is("Romaguera-Crona"));
        assertThat(userResponse.getPosts().size(), is(0));
    }

    @Test(expected = ExecutionException.class)
    public void testGetUserWithPosts_userTimeOut() throws Exception {
        doAnswer(invocation -> {
            Thread.sleep(2000);
            return null;
        }).when(typicodeClient).getUserById(any());

        userCommand.observe()
                .zipWith(userPostCommand.observe(),
                        (UserDTO u, List<UserPostDTO> p) -> UserResponse.builder()
                                .user(u)
                                .posts(p)
                                .build())
                .toBlocking().toFuture().get();

        fail("Exception is not thrown");
    }

    @Test(expected = ExecutionException.class)
    public void testGetUserWithPosts_userFail() throws Exception {
        when(typicodeClient.getUserById(any())).thenThrow(new RuntimeException("UserResponse command failed"));

        userCommand.observe()
                .zipWith(userPostCommand.observe(),
                        (UserDTO u, List<UserPostDTO> p) -> UserResponse.builder()
                                .user(u)
                                .posts(p)
                                .build())
                .toBlocking().toFuture().get();

        fail("Exception is not thrown");
    }
}
