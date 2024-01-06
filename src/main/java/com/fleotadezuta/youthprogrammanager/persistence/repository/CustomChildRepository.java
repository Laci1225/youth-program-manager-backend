package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import reactor.core.publisher.Flux;

public interface CustomChildRepository {
    Flux<ChildDocument> findByFullName(String name);
}
