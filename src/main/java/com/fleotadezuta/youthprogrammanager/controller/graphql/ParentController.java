package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
@Slf4j
public class ParentController {

    private final ChildParentFacade childParentFacade;

    @QueryMapping("getAllParents")
    public Flux<ParentUpdateDto> getAllParents() {
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
