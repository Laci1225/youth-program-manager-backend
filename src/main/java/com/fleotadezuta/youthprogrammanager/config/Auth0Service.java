package com.fleotadezuta.youthprogrammanager.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Service
@AllArgsConstructor
public class Auth0Service {

    private final RestTemplate restTemplate;

    @Value("${management.audience}")
    private static String API;


    public Map<String, String> getUserInfo(String userId) {

        var accessToken = "";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        Properties properties = new Properties();
        InputStream input = Main.class.getClassLoader().getResourceAsStream("application-local.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String auth0ManagementApiUrl = properties.getProperty("management.audience").concat("users/");
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
