package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
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
public class ChildController {

    private final ChildService childService;
    private final ChildParentFacade childParentFacade;

    @QueryMapping("getAllChildren")
    public Flux<ChildDto> getAllChildren() {
        return childService.getAllChildren()
                .doOnComplete(() -> log.info("All children fetched successfully"));
    }

    @QueryMapping("getChildById")
    public Mono<ChildDto> getChildById(@Argument String id) {
        return childService.getChildById(id)
                .doOnSuccess(childDto -> log.info("Retrieved Child by ID: " + id));
    }

    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@Valid @RequestBody @Argument ChildDto child) {
        return childParentFacade.addChild(child)
                .doOnSuccess(childDto -> log.info("Added Child with data: " + childDto));
    }

    @MutationMapping("updateChild")
    public Mono<ChildDto> updateChild(@Argument String id, @Valid @RequestBody @Argument ChildDto child) {
        return childService.updateChild(id, child)
                .doOnSuccess(childDto -> log.info("Updated Child with ID: " + id));
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