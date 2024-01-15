package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.TicketService;
import com.fleotadezuta.youthprogrammanager.service.TicketTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@AllArgsConstructor
@Slf4j
public class TicketChildTicketTypeFacade {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketTypeService ticketTypeService;
    private final TicketTypeMapper ticketTypeMapper;
    private final ChildService childService;
    private final ChildMapper childMapper;

    public Flux<TicketTypeDto> getPotentialTicketTypes(String name) {
        return ticketTypeService.findByName(name);
    }
    
    private Mono<Tuple2<ChildDto, TicketTypeDto>> getChildAndTicketType(String childId, String ticketTypeId) {
        return Mono.zip(
                childService.findById(childId).map(childMapper::fromChildDocumentToChildDto),
                ticketTypeService.findById(ticketTypeId).map(ticketTypeMapper::fromTicketTypeDocumentToTicketTypeDto)
        );
    }

    private Mono<TicketDto> mapToTicketDto(TicketDocument ticketDocument, ChildDto child, TicketTypeDto ticketType) {
        return Mono.just(ticketMapper.fromTicketDocumentToTicketDto(ticketDocument, child, ticketType));
    }

    public Flux<TicketDto> getAllTickets() {
        return ticketService.findAll()
                .flatMap(ticketDocument ->
                        getChildAndTicketType(ticketDocument.getChildId(), ticketDocument.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2()))
                );
    }

    public Mono<TicketDto> getTicketById(String id) {
        return ticketService.findById(id)
                .flatMap(ticketDocument ->
                        getChildAndTicketType(ticketDocument.getChildId(), ticketDocument.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2()))
                );
    }

    public Mono<TicketDto> addTicket(TicketCreationDto ticketCreationDto) {
        return Mono.just(ticketCreationDto)
                .map(ticketMapper::fromTicketCreationDtoToTicketDocument)
                .flatMap(ticketService::save)
                .flatMap(ticketDocument ->
                        getChildAndTicketType(ticketDocument.getChildId(), ticketDocument.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2()))
                );
    }

    public Mono<TicketDto> deletedTicket(String id) {
        return ticketService.findById(id)
                .flatMap(ticket -> ticketService.deleteById(id).then(Mono.just(ticket)))
                .flatMap(ticketDocument ->
                        getChildAndTicketType(ticketDocument.getChildId(), ticketDocument.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2()))
                );
    }

    public Mono<TicketDto> updateTicket(String id, TicketUpdateDto ticketUpdateDto) {
        return Mono.just(ticketUpdateDto)
                .map(ticketMapper::fromTicketUpdateDtoToTicketDocument)
                .flatMap(ticketDoc -> {
                    ticketDoc.setId(id);
                    return ticketService.save(ticketDoc);
                })
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDoc, tuple.getT1(), tuple.getT2()))
                );
    }
}
