package com.fleotadezuta.youthprogrammanager.config;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Properties;

@Service
@AllArgsConstructor
public class Auth0Service {

    private final RestTemplate restTemplate;


    public Map<String, String> getUserInfo(String userId, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        Properties properties = System.getProperties();
        String auth0ManagementApiUrl = properties.getProperty("okta.oauth2.audience") + "/users/";
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                auth0ManagementApiUrl + userId,
                HttpMethod.GET,
                entity,
                Map.class,
                userId
        );

        return (Map<String, String>) responseEntity.getBody().get("app_metadata");
    }
}
