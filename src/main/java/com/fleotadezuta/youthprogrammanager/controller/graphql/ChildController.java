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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@AllArgsConstructor
@Slf4j
public class ChildController {

    private final ChildService childService;
    private final ChildParentFacade childParentFacade;

    @PreAuthorize("hasAuthority('list:children')")
    @QueryMapping("getAllChildren")
    public Flux<ChildDto> getAllChildren(GraphQLContext context) {
        return childService.getAllChildren(new UserDetails(context));
    }

    @PreAuthorize("hasAuthority('read:children')")
    @QueryMapping("getChildById")
    public Mono<ChildWithParentsDto> getChildById(@Argument String id) {
        return childParentFacade.getChildById(id)
                .doOnSuccess(childDto -> log.info("Retrieved Child by ID: " + childDto));
    }

    @PreAuthorize("hasAuthority('create:children')")
    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@Valid @RequestBody @Argument ChildCreateDto child) {
        return childParentFacade.addChild(child)
                .doOnSuccess(childDto -> log.info("Added Child with data: " + childDto));
    }

    @PreAuthorize("hasAuthority('update:children')")
    @MutationMapping("updateChild")
    public Mono<ChildUpdateDto> updateChild(@Valid @RequestBody @Argument ChildUpdateDto child) {
        return childParentFacade.updateChild(child)
                .doOnSuccess(childUpdateDto -> log.info("Updated Child with ID: " + childUpdateDto.getId()));
    }

    @PreAuthorize("hasAuthority('delete:children')")
    @MutationMapping("deleteChild")
    public Mono<ChildDto> deleteChild(@Argument String id) {
        return childParentFacade.deleteChild(id)
                .doOnSuccess(deletedChild -> log.info("Deleted Child with ID: " + deletedChild.getId()));
    }

    @QueryMapping("getPotentialParents")
    public Flux<ParentDto> getPotentialParents(@Argument String name) {
        return childParentFacade.getPotentialParents(name)
                .doOnNext(parent -> log.info("Parent with name " + parent.getGivenName() + " " + parent.getFamilyName() + " fetched successfully"));
    }

}