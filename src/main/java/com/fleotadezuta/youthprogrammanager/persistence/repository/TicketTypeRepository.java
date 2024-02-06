package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TicketTypeRepository extends ReactiveMongoRepository<TicketTypeDocument, String> {
    Flux<TicketTypeDocument> findAllByName(String name);
}
