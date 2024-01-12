package com.fleotadezuta.youthprogrammanager.persistence.repository;

import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import reactor.core.publisher.Flux;

public interface CustomTicketTypeRepository {
    Flux<TicketTypeDocument> findByName(String name);

}
