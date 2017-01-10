package com.company.gateway.api.model;

import com.company.gateway.dto.UserDTO;
import com.company.gateway.dto.UserPostDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private UserDTO user;
    private List<UserPostDTO> posts;
}
