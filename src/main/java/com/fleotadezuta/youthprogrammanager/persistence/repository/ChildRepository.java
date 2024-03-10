package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface ChildRepository extends ReactiveMongoRepository<ChildDocument, String>, CustomChildRepository {
    Flux<ChildDocument> findChildDocumentsByRelativeParents_Id(String relativeParentsId);

}
