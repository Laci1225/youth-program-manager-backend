package com.fleotadezuta.youthprogrammanager.config;

import com.sun.tools.javac.Main;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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


    public Map<String, String> getUserInfo(String userId, String accessToken) {
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
        String auth0ManagementApiUrl = properties.getProperty("okta.oauth2.audience").concat("users/");
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
