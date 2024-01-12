package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TicketService {
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;


    public Flux<TicketDocument> findAll() {
        return ticketRepository.findAll();
    }

    public Mono<TicketDocument> findById(String id) {
        return ticketRepository.findById(id);
    }

    public Mono<TicketDocument> save(TicketDocument ticketDocument) {
        return ticketRepository.save(ticketDocument);
    }

    public Mono<Void> deleteById(String id) {
        return ticketRepository.deleteById(id);
    }
}
