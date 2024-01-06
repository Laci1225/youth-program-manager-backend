package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import reactor.core.publisher.Flux;

public interface CustomParentRepository {

    Flux<ParentDocument> findByFullName(String name);

}
