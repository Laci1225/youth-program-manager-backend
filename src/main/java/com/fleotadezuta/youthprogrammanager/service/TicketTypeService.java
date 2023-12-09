package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.repository.TicketTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TicketTypeService {
    private final TicketTypeMapper ticketTypeMapper;
    private final TicketTypeRepository ticketTypeRepository;

    public Flux<TicketTypeDto> getAllTickets() {
        return ticketTypeRepository.findAll()
                .map(ticketTypeMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketTypeDto> getTicketById(String id) {
        return ticketTypeRepository.findById(id)
                .map(ticketTypeMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketTypeDto> addTicket(TicketTypeDto ticketTypeDto) {
        return Mono.just(ticketTypeDto)
                .map(ticketTypeMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketTypeRepository::save)
                .map(ticketTypeMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketTypeDto> deleteTicket(String id) {
        return ticketTypeRepository.findById(id)
                .flatMap(ticker -> ticketTypeRepository.deleteById(id)
                        .then(Mono.just(ticker)))
                .map(ticketTypeMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketTypeDto> updateTicket(String id, TicketTypeDto tickerDto) {
        return Mono.just(tickerDto)
                .map(ticketTypeMapper::fromTicketDtoToTicketDocument)
                .flatMap(tickerDoc -> {
                    tickerDoc.setId(id);
                    return ticketTypeRepository.save(tickerDoc);
                })
                .map(ticketTypeMapper::fromTicketDocumentToTicketDto);
    }

}
