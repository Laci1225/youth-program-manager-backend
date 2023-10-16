package com.fleotadezuta.youthprogrammanager.controller;

import com.fleotadezuta.youthprogrammanager.persistance.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "children")
@AllArgsConstructor
public class ChildController {

    private ChildService childService;

    @GetMapping
    public Flux<ChildDocument> getAllChildren(){
        return childService.children();
    }

    @PostMapping
    public Mono<ChildDocument> addChild(@RequestBody ChildDocument childDocument){
        return childService.addChild(childDocument);
    }
}
