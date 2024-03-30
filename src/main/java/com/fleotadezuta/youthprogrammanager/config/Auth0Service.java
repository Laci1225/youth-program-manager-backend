package com.fleotadezuta.youthprogrammanager.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.model.AppMetadata;
import com.fleotadezuta.youthprogrammanager.model.UserData;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0Service {

    @Value("${auth0.management.audience}")
    private String audience;
    @Value("${auth0.management.api.token}")
    private String accessToken;
    public static final OkHttpClient client = new OkHttpClient();

    public AppMetadata getUserInfo(String userId) {
        String auth0ManagementApiUrl = audience + "users/" + userId;
        Request request = new Request.Builder()
                .url(auth0ManagementApiUrl)
                .header("Authorization", "Bearer " + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                var responseBody = new ObjectMapper().readValue(response.body().string(), Map.class);
                return new ObjectMapper().convertValue(responseBody.get("app_metadata"), AppMetadata.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new AppMetadata();
    }

    public void createUsers(String email, String id, String givenName, String familyName, Role role) {
        MediaType mediaType = MediaType.parse("application/json");
        UserData userData = new UserData(
                email,
                new AppMetadata(id, role.name()),
                familyName,
                givenName,
                "email");
        RequestBody body;
        try {
            body = RequestBody.create(
                    new ObjectMapper().writeValueAsString(userData),
                    mediaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder()
                .url(audience + "users")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String userId = jsonNode.get("user_id").asText();
                RequestBody body2 = RequestBody.create(
                        "{\"users\":[\"" + userId + "\"]}",
                        mediaType);
                Request request2 = new Request.Builder()
                        .url(audience + "roles/" + role.getRoleId() + "/users")
                        .method("POST", body2)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                Response response2 = client.newCall(request2).execute();
                log.error(response2.body().string());
            } else {
                log.error("Error: " + response.code() + ", " + response.message());
                log.error(response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(String authUser) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        Request request;
        request = new Request.Builder()
                .url(audience + "users/" + URLEncoder.encode(authUser, StandardCharsets.UTF_8))
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            log.error(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
