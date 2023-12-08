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

    public Mono<TicketDto> deleteTicket(String id) {
        return ticketRepository.findById(id)
                .flatMap(ticker -> ticketRepository.deleteById(id)
                        .then(Mono.just(ticker)))
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> updateTicket(String id, TicketDto tickerDto) {
        return Mono.just(tickerDto)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(tickerDoc -> {
                    tickerDoc.setId(id);
                    return ticketRepository.save(tickerDoc);
                })
                .map(ticketMapper::fromTicketDocumentToTicketDto);
    }

}
