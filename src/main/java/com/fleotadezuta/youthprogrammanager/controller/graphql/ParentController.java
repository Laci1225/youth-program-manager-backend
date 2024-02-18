package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.sun.tools.javac.Main;
import jakarta.validation.Valid;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Controller
@AllArgsConstructor
@Slf4j
public class ParentController {

    private final ChildParentFacade childParentFacade;

    @QueryMapping("getAllParents")
    public Flux<ParentUpdateDto> getAllParents() {
        var accesstoken = "";
        HttpResponse<String> response = Unirest.post("https://dev-wnuf5ensk4dnqucn.eu.auth0.com/api/v2/users/auth0%7C65b6462677cdcbbf47524ce6/roles")
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
        log.error(response.getBody());
        return childParentFacade.getAllParents()
                .doOnComplete(() -> log.info("All parents fetched successfully"));
    }

    @QueryMapping("getParentById")
    public Mono<ParentWithChildrenDto> getParentById(@Argument String id) {
        return childParentFacade.getParentById(id)
                .doOnSuccess(parentDto -> log.info("Retrieved Parent by ID: " + id));
    }

    @MutationMapping("addParent")
    public Mono<ParentDto> addParent(@Valid @RequestBody @Argument ParentCreateDto parent) {
        return childParentFacade.addParent(parent)
                .doOnSuccess(parentDto -> log.info("Added Parent with data: " + parentDto));
    }

    @MutationMapping("updateParent")
    public Mono<ParentDto> updateParent(@Valid @RequestBody @Argument ParentUpdateDto parent) {
        return childParentFacade.updateParent(parent)
                .doOnSuccess(parentDto -> log.info("Updated Parent: " + parentDto));
    }

    @MutationMapping("deleteParent")
    public Mono<ParentDto> deleteParent(@Argument String id) {
        return childParentFacade.deleteParent(id)
                .doOnSuccess(deletedParent -> log.info("Deleted Parent with ID: " + deletedParent.getId()));
    }

    @QueryMapping("getPotentialChildren")
    public Flux<ChildDto> getPotentialParents(@Argument String name) {
        return childParentFacade.getPotentialChildren(name)
                .doOnNext(parent -> log.info("Child with name " + parent.getGivenName() + " " + parent.getFamilyName() + " fetched successfully"));
    }
}
