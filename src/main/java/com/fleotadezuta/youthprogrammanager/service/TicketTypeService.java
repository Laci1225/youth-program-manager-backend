package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.repository.TicketTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TicketTypeService {
    private final TicketTypeMapper ticketTypeMapper;
    private final TicketTypeRepository ticketTypeRepository;

    public Flux<TicketTypeDto> getAllTicketTypes(UserDetails userDetails) {
        if (!userDetails.getUserType().equals("ADMIN")) {
            return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not authorized to report participation"));
        }
        return ticketTypeRepository.findAll()
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> getTicketTypeById(UserDetails userDetails, String id) {
        if (!userDetails.getUserType().equals("ADMIN")) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not authorized to report participation"));
        }
        return ticketTypeRepository.findById(id)
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> addTicketType(UserDetails userDetails, TicketTypeDto ticketTypeDto) {
        if (!userDetails.getUserType().equals("ADMIN")) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not authorized to report participation"));
        }
        return Mono.just(ticketTypeDto)
                .map(ticketTypeMapper::fromTicketTypeDtoToTicketTypeDocument)
                .flatMap(ticketTypeRepository::save)
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> deletedTicketType(UserDetails userDetails, String id) {
        if (!userDetails.getUserType().equals("ADMIN")) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not authorized to report participation"));
        }
        return ticketTypeRepository.findById(id)
                .flatMap(ticketType -> ticketTypeRepository.deleteById(id)
                        .then(Mono.just(ticketType)))
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> updateTicketType(UserDetails userDetails, String id, TicketTypeDto ticketTypeDto) {
        if (!userDetails.getUserType().equals("ADMIN")) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not authorized to report participation"));
        }
        return Mono.just(ticketTypeDto)
                .map(ticketTypeMapper::fromTicketTypeDtoToTicketTypeDocument)
                .flatMap(ticketTypeDoc -> {
                    ticketTypeDoc.setId(id);
                    return ticketTypeRepository.save(ticketTypeDoc);
                })
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Flux<TicketTypeDto> findByName(String name) {
        return ticketTypeRepository.findAllByNameContaining(name).map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }

    public Mono<TicketTypeDto> findById(String id) {
        return ticketTypeRepository.findById(id)
                .map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto);
    }
}
