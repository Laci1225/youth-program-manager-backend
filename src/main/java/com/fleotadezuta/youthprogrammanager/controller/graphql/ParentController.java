package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import graphql.GraphQLContext;
import jakarta.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;


@Controller
@Slf4j
@RequiredArgsConstructor
public class ParentController {

    private final ChildParentFacade childParentFacade;

    @Value("${auth0.management.audience}")
    private String audience;
    @Value("${auth0.management.api.token}")
    private String accessToken;

    @PreAuthorize("hasAuthority('list:parents')")
    @QueryMapping("getAllParents")
    public Flux<ParentUpdateDto> getAllParents() {
       /* var accesstoken = "";
        HttpResponse<String> response = Unirest.post(audience+"users/auth0%7C65b6462677cdcbbf47524ce6/roles")
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + accesstoken)
                .header("cache-control", "no-cache")
                .body("{\"roles\":[\"rol_ZVbZjai55NpwOwhV\",\"rol_Mjt9yu2PlPadWRn5\"]}")
                .asString();
        //everything iwth id nothing with name
        System.out.println("Response code: " + response.getStatus());
        System.out.println("Response body: " + response.getBody());
        log.error(response.getHeaders().toString());
        log.error(response.getCookies().toString());
        log.error(response.getBody());*/
        return childParentFacade.getAllParents()
                .doOnComplete(() -> log.info("All parents fetched successfully"));
    }

    @QueryMapping("getParentById")
    public Mono<ParentWithChildrenDto> getParentById(GraphQLContext context, @Argument String id) {
        return childParentFacade.getParentById(new UserDetails(context), id)
                .doOnSuccess(parentDto -> log.info("Retrieved Parent by ID: " + id));
    }

    @MutationMapping("addParent")
    public Mono<ParentDto> addParent(@Valid @RequestBody @Argument ParentCreateDto parent) {
        return childParentFacade.addParent(parent)
                .doOnSuccess(parentDto -> {
                    log.info("Added Parent with data: " + parentDto);

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    okhttp3.RequestBody body = okhttp3.RequestBody.create(
                            "{\"email\":\"user1q131911@example.com\"," +
                                    "\"connection\":\"Username-Password-Authentication\"," +
                                    "\"app_metadata\":{\"app_user_id\":\"" + parentDto.getId() + "\", \"app_user_type\":\"PARENT\"}," +
                                    "\"given_name\":\"" + parentDto.getGivenName() + "\"," +
                                    "\"family_name\":\"" + parentDto.getFamilyName() + "\"," +
                                    "\"password\":\"Example123!\"}",
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
                            String userId = jsonNode.get("identities").get(0).get("user_id").asText();
                            okhttp3.RequestBody body2 = okhttp3.RequestBody.create(
                                    "{\"users\":[\"auth0|" + userId + "\"]}",
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

                });
    }

    @MutationMapping("updateParent")
    public Mono<ParentDto> updateParent(GraphQLContext context, @Valid @RequestBody @Argument ParentUpdateDto parent) {
        return childParentFacade.updateParent(new UserDetails(context), parent)
                .doOnSuccess(parentDto -> log.info("Updated Parent: " + parentDto));
    }

    @MutationMapping("deleteParent")
    public Mono<ParentDto> deleteParent(GraphQLContext context, @Argument String id) {
        return childParentFacade.deleteParent(new UserDetails(context), id)
                .doOnSuccess(deletedParent -> log.info("Deleted Parent with ID: " + deletedParent.getId()));
    }

    @QueryMapping("getPotentialChildren")
    public Flux<ChildDto> getPotentialParents(@Argument String name) {
        return childParentFacade.getPotentialChildren(name)
                .doOnNext(parent -> log.info("Child with name " + parent.getGivenName() + " " + parent.getFamilyName() + " fetched successfully"));
    }
}
