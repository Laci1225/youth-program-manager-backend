package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketDto;
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


    public Flux<TicketDto> findAll() {
        return ticketRepository.findAll()
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> findById(String id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::fromTicketDocumentToTicketDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Ticket not found")));
    }

    public Mono<TicketDto> save(TicketDocument ticketDocument) {
        return ticketRepository.save(ticketDocument)
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<Void> deleteById(String id) {
        return ticketRepository.deleteById(id);
    }

    public Flux<TicketDto> findAllByChildId(String userId) {
        return ticketRepository.findAllByChildId(userId)
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }
}
