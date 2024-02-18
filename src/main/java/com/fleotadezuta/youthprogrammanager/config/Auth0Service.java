package com.fleotadezuta.youthprogrammanager.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Auth0Service {

    private final RestTemplate restTemplate;

    @Value("${auth0.management.audience}")
    private String audience;
    @Value("${auth0.management.api.token}")
    private String accessToken;


    public Map<String, String> getUserInfo(String userId) {


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String auth0ManagementApiUrl = audience + "users/";
        var responseEntity = restTemplate.exchange(
                auth0ManagementApiUrl + userId,
                HttpMethod.GET,
                entity,
                Map.class,
                userId
        );
        var responseBody = responseEntity.getBody();
        if (responseBody != null) {
            var appMetadata = responseEntity.getBody().get("app_metadata");
            if (appMetadata != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(appMetadata, new TypeReference<Map<String, String>>() {
                });
            }
        }
        return Map.of();
    }
}
