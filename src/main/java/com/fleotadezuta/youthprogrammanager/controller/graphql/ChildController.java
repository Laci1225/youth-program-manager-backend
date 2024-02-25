package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import graphql.GraphQLContext;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Controller
@AllArgsConstructor
@Slf4j
public class ChildController {

    private final ChildService childService;
    private final ChildParentFacade childParentFacade;

    @QueryMapping("getAllChildren")
    public Flux<ChildDto> getAllChildren(Authentication authentication, GraphQLContext context) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt principal) {
            List<String> permissions = principal.getClaimAsStringList("permissions");
            System.out.println("Permissions: " + permissions);
            if (permissions.contains("list:children")) {
                return childService.getAllChildren(new UserDetails(context));
            } else {
                return Flux.error(new IllegalArgumentException("User not authorized"));
            }
        } else {
            return Flux.error(new IllegalArgumentException("User not authenticated"));
        }
    }

    @QueryMapping("getChildById")
    public Mono<ChildWithParentsDto> getChildById(Authentication authentication, GraphQLContext context, @Argument String id) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt principal) {
            List<String> permissions = principal.getClaimAsStringList("permissions");
            System.out.println("Permissions: " + permissions);
            if (permissions.contains("read:children")) {
                return childParentFacade.getChildById(new UserDetails(context), id)
                        .doOnSuccess(childDto -> log.info("Retrieved Child by ID: " + childDto));
            } else {
                return Mono.error(new IllegalArgumentException("User not authorized"));
            }
        } else {
            return Mono.error(new IllegalArgumentException("User not authenticated"));
        }
    }

    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@Valid @RequestBody @Argument ChildCreateDto child) {
        return childParentFacade.addChild(child)
                .doOnSuccess(childDto -> log.info("Added Child with data: " + childDto));
    }

    @MutationMapping("updateChild")
    public Mono<ChildUpdateDto> updateChild(GraphQLContext context, @Valid @RequestBody @Argument ChildUpdateDto child) {
        return childService.updateChild(new UserDetails(context), child)
                .doOnSuccess(childUpdateDto -> log.info("Updated Child with ID: " + childUpdateDto.getId()));
    }

    @MutationMapping("deleteChild")
    public Mono<ChildDto> deleteChild(GraphQLContext context, @Argument String id) {
        return childService.deleteChild(new UserDetails(context), id)
                .doOnSuccess(deletedChild -> log.info("Deleted Child with ID: " + deletedChild.getId()));
    }

    @QueryMapping("getPotentialParents")
    public Flux<ParentDto> getPotentialParents(@Argument String name) {
        return childParentFacade.getPotentialParents(name)
                .doOnNext(parent -> log.info("Parent with name " + parent.getGivenName() + " " + parent.getFamilyName() + " fetched successfully"));
    }

}