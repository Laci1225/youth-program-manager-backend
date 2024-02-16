package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import jakarta.validation.Valid;
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

import java.nio.file.AccessDeniedException;


@Controller
@AllArgsConstructor
@Slf4j
public class ChildController {

    private final ChildService childService;
    private final ChildParentFacade childParentFacade;
    private final Auth0Service auth0Service;

    @QueryMapping("getAllChildren")
    public Flux<ChildDto> getAllChildren() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.error(authentication.toString());
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            String accessToken = jwtAuthenticationToken.getToken().getTokenValue();
            log.error(accessToken);
            var a = auth0Service.getUserInfo(authentication.getName(), accessToken);
            //{appId=YOUR_APP_ID, role=ADMIN}
            if (a.get("role").equals("ADMIN")) {
                return childService.getAllChildren();
            } else {
                throw new AccessDeniedException("User doesn't have permission to read children");
            }
        }
        return childService.getAllChildren();
    }

    @QueryMapping("getChildById")
    public Mono<ChildWithParentsDto> getChildById(@Argument String id) {
        return childParentFacade.getChildById(id)
                .doOnSuccess(childDto -> log.info("Retrieved Child by ID: " + childDto));
    }

    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@Valid @RequestBody @Argument ChildCreateDto child) {
        return childParentFacade.addChild(child)
                .doOnSuccess(childDto -> log.info("Added Child with data: " + childDto));
    }

    @MutationMapping("updateChild")
    public Mono<ChildUpdateDto> updateChild(@Valid @RequestBody @Argument ChildUpdateDto child) {
        return childService.updateChild(child)
                .doOnSuccess(childUpdateDto -> log.info("Updated Child with ID: " + childUpdateDto.getId()));
    }

    @MutationMapping("deleteChild")
    public Mono<ChildDto> deleteChild(@Argument String id) {
        return childService.deleteChild(id)
                .doOnSuccess(deletedChild -> log.info("Deleted Child with ID: " + deletedChild.getId()));
    }

    @QueryMapping("getPotentialParents")
    public Flux<ParentDto> getPotentialParents(@Argument String name) {
        return childParentFacade.getPotentialParents(name)
                .doOnNext(parent -> log.info("Parent with name " + parent.getGivenName() + " " + parent.getFamilyName() + " fetched successfully"));
    }

}