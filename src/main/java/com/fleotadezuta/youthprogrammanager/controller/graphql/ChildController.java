package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@AllArgsConstructor
public class ChildController {

    private final ChildService childService;

    @QueryMapping("children")
    public Flux<ChildDto> getAllChildren() {
        return childService.getAllChildren();
    }

    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@Valid @RequestBody @Argument ChildDto child) {
        return childService.addChild(child);
    }
}
