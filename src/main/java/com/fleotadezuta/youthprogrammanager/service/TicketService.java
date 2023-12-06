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
    private final TicketMapper tickerMapper;
    private final TicketRepository tickerRepository;

    public Flux<TicketDto> getAllTickets() {
        return tickerRepository.findAll()
                .map(tickerMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> getTicketById(String id) {
        return tickerRepository.findById(id)
                .map(tickerMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> addTicket(TicketDto tickerDto) {
        var tickerDoc = tickerMapper.fromTicketDtoToTicketDocument(tickerDto);
        return tickerRepository.save(tickerDoc)
                .map(tickerMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> deleteTicket(String id) {
        return tickerRepository.findById(id)
                .flatMap(ticker -> tickerRepository.deleteById(id)
                        .then(Mono.just(ticker)))
                .map(tickerMapper::fromTicketDocumentToTicketDto);
    }

    public Mono<TicketDto> updateTicket(String id, TicketDto tickerDto) {
        return Mono.just(tickerDto)
                .map(tickerMapper::fromTicketDtoToTicketDocument)
                .flatMap(tickerDoc -> {
                    tickerDoc.setId(id);
                    return tickerRepository.save(tickerDoc);
                })
                .map(tickerMapper::fromTicketDocumentToTicketDto);
    }

}
