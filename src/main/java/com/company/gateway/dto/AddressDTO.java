package com.company.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private Geo geo;

    @Data
    @AllArgsConstructor(suppressConstructorProperties = true)
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geo {
        private Float lat;
        private Float lng;
    }
}
