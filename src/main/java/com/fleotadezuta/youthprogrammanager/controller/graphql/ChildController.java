package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.ChildDto;
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

    @QueryMapping("getAllChildren")
    public Flux<ChildDto> getAllChildren() {
        var result = childService.getAllChildren();
        return result.doOnComplete(() -> log.info("All children fetched successfully"));
    }

    @QueryMapping("getChildById")
    public Mono<ChildDto> getChildById(@Argument String id) {
        return childService.getChildById(id);
    }

    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@Valid @RequestBody @Argument ChildDto child) {
        return childService.addChild(child);
    }

    @MutationMapping("deleteChild")
    public Mono<ChildDto> deleteChild(@Argument String id) {
        return childService.deleteChild(id)
                .doOnSuccess(deletedChild ->
                        log.info("Deleting Child with ID: " + deletedChild.getId()));
    }
}
