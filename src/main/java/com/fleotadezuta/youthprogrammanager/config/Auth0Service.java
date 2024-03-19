package com.fleotadezuta.youthprogrammanager.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
            System.out.println(response);
            if (response.isSuccessful()) {
                var responseBody = new ObjectMapper().readValue(response.body().string(), Map.class);
                return new ObjectMapper().convertValue(responseBody.get("app_metadata"), AppMetadata.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AppMetadata();
    }

    public void addAuth0Parent(ParentDto parentDto) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(
                "{\"email\":\"" + parentDto.getEmail() + "\"," +
                        "\"connection\":\"email\"," +
                        "\"app_metadata\":{\"app_user_id\":\"" + parentDto.getId() + "\", \"app_user_type\":\"PARENT\"}," +
                        "\"given_name\":\"" + parentDto.getGivenName() + "\"," +
                        "\"family_name\":\"" + parentDto.getFamilyName() + "\"}",
                mediaType);
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
                log.error("User created with id: " + userId);
                RequestBody body2 = RequestBody.create(
                        "{\"users\":[\"" + userId + "\"]}",
                        mediaType);
                Request request2 = new Request.Builder()
                        .url(audience + "roles/rol_Mjt9yu2PlPadWRn5/users")
                        .method("POST", body2)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                Response response2 = client.newCall(request2).execute();
                log.error(response2.body().string());
            } else {
                System.err.println("Error: " + response.code() + ", " + response.message());
                System.err.println(response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
