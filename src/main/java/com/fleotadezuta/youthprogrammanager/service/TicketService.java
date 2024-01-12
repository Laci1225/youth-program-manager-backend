package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketDto;
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

    public Flux<TicketDto> getAllTickets() {
        return ticketRepository.findAll()
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> getTicketById(String id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> addTicket(TicketDto ticketDto) {
        return Mono.just(ticketDto)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketRepository::save)
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> deletedTicket(String id) {
        return ticketRepository.findById(id)
                .flatMap(ticket -> ticketRepository.deleteById(id)
                        .then(Mono.just(ticket)))
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> updateTicket(String id, TicketDto ticketDto) {
        return Mono.just(ticketDto)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketDoc -> {
                    ticketDoc.setId(id);
                    return ticketRepository.save(ticketDoc);
                })
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }
}
