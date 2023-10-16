package com.fleotadezuta.youthprogrammanager.persistance.repository;

import com.fleotadezuta.youthprogrammanager.persistance.document.ChildDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends ReactiveMongoRepository<ChildDocument, String> {
}
