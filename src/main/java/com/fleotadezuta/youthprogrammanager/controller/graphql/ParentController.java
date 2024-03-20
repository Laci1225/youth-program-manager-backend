package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import graphql.GraphQLContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@Slf4j
@RequiredArgsConstructor
public class ParentController {

    private final ChildParentFacade childParentFacade;

    @PreAuthorize("hasAuthority('list:parents')")
    @QueryMapping("getAllParents")
    public Flux<ParentUpdateDto> getAllParents() {
        return childParentFacade.getAllParents()
                .doOnComplete(() -> log.info("All parents fetched successfully"));
    }

    @PreAuthorize("hasAuthority('read:parents')")
    @QueryMapping("getParentById")
    public Mono<ParentWithChildrenDto> getParentById(GraphQLContext context, @Argument String id) {
        return childParentFacade.getParentById(new UserDetails(context), id)
                .doOnSuccess(parentDto -> log.info("Retrieved Parent: " + parentDto));
    }

    @PreAuthorize("hasAuthority('create:parents')")
    @MutationMapping("addParent")
    public Mono<ParentDto> addParent(@Valid @RequestBody @Argument ParentCreateDto parent) {
        return childParentFacade.addParent(parent)
                .doOnSuccess(parentDto -> log.info("Added Parent with data: " + parentDto));
    }

    @PreAuthorize("hasAuthority('update:parents')")
    @MutationMapping("updateParent")
    public Mono<ParentDto> updateParent(GraphQLContext context, @Valid @RequestBody @Argument ParentUpdateDto parent) {
        return childParentFacade.updateParent(new UserDetails(context), parent)
                .doOnSuccess(parentDto -> log.info("Updated Parent: " + parentDto));
    }

    @PreAuthorize("hasAuthority('delete:parents')")
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
