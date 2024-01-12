package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
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

    public Flux<TicketTypeDto> getAllTicketTypes() {
        return ticketTypeRepository.findAll()
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> getTicketTypeById(String id) {
        return ticketTypeRepository.findById(id)
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> addTicketType(TicketTypeDto ticketTypeDto) {
        return Mono.just(ticketTypeDto)
                .map(ticketTypeMapper::fromTicketTypeDtoToTicketTypeDocument)
                .flatMap(ticketTypeRepository::save)
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> deletedTicketType(String id) {
        return ticketTypeRepository.findById(id)
                .flatMap(ticketType -> ticketTypeRepository.deleteById(id)
                        .then(Mono.just(ticketType)))
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> updateTicketType(String id, TicketTypeDto ticketTypeDto) {
        return Mono.just(ticketTypeDto)
                .map(ticketTypeMapper::fromTicketTypeDtoToTicketTypeDocument)
                .flatMap(ticketTypeDoc -> {
                    ticketTypeDoc.setId(id);
                    return ticketTypeRepository.save(ticketTypeDoc);
                })
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Flux<TicketTypeDto> findByName(String name) {
        return ticketTypeRepository.findByName(name).map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDocument> findById(String id) {
        return ticketTypeRepository.findById(id);
    }
}
