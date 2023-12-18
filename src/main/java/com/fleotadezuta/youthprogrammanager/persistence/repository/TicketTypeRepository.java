package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TicketTypeRepository extends ReactiveMongoRepository<TicketTypeDocument, String> {
}
