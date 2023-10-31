package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends ReactiveMongoRepository<ChildDocument, String> {
}
