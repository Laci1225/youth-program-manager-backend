package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ParentRepository extends ReactiveMongoRepository<ParentDocument, String> {
}
