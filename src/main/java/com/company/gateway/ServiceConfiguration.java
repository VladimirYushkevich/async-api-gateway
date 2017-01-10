package com.company.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ObjectMapper unirestObjectMapper() {
        ObjectMapper unirestObjectMapper = new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper objectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return objectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return objectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Unirest.setObjectMapper(unirestObjectMapper);

        return unirestObjectMapper;
    }
}
