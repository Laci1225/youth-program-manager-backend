package com.fleotadezuta.youthprogrammanager.controller;

import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.persistance.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@AllArgsConstructor
public class ChildController {

    private final ChildService childService;

    @QueryMapping("children")
    public Flux<ChildDto> getAllChildren(){
        return childService.children();
    }

    @MutationMapping("addChild")
    public Mono<ChildDto> addChild(@RequestBody ChildDocument childDocument){
        return childService.addChild(childDocument);
    }
}
