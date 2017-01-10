package com.company.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    private AddressDTO address;
    private CompanyDTO company;
}
