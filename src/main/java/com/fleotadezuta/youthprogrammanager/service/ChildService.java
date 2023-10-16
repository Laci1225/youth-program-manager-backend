package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.persistance.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistance.repository.ChildRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class ChildService {

    private ChildRepository childRepository;


    public Flux<ChildDocument> children(){
        return childRepository.findAll();
    }
    public Mono<ChildDocument> addChild(ChildDocument childDocument) {
        return childRepository.save(childDocument);

    }
}
