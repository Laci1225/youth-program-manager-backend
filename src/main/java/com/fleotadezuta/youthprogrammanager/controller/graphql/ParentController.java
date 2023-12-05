package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
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

    private final ParentService parentService;

    @QueryMapping("getAllParents")
    public Flux<ParentDto> getAllParents() {
        return parentService.getAllParents()
                .doOnComplete(() -> log.info("All parents fetched successfully"));
    }

    @QueryMapping("getParentById")
    public Mono<ParentDto> getParentById(@Argument String id) {
        return parentService.getParentById(id);
    }


    @MutationMapping("addParent")
    public Mono<ParentDto> addParent(@Valid @RequestBody @Argument ParentDto parent) {
        return parentService.addParent(parent)
                .doOnSuccess((parentDto) -> log.info("Parent with data:" + parentDto + " fetched successfully"));
    }

    @MutationMapping("deleteParent")
    public Mono<ParentDto> deleteParent(@Argument String id) {
        return parentService.deleteParent(id)
                .doOnSuccess(deletedParent ->
                        log.info("Deleted Parent with ID:" + deletedParent.getId()));
    }
}
