package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends ReactiveMongoRepository<ParentDocument, String> {


    Flux<ParentDocument> findByGivenNameStartingWithIgnoreCase(String name);

    Flux<ParentDocument> findByFamilyNameStartingWithIgnoreCase(String name);

}
