package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.TicketService;
import com.fleotadezuta.youthprogrammanager.service.TicketTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@AllArgsConstructor
public class TicketChildTicketTypeFacade {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketTypeService ticketTypeService;
    private final TicketTypeMapper ticketTypeMapper;
    private final ChildService childService;
    private final ChildMapper childMapper;

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

    public Mono<TicketDto> addTicket(TicketDocument ticketDocument) {//todo creationDto
        return Mono.just(ticketDocument)
                .flatMap(ticketService::save)
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDoc, tuple.getT1(), tuple.getT2()))
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

    public Mono<TicketDto> updateTicket(String id, TicketDto ticketDto) {
        return Mono.just(ticketDto)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketDoc -> {
                    ticketDoc.setId(id);
                    return ticketService.save(ticketDoc);
                })
                .flatMap(ticketDocument ->
                        getChildAndTicketType(ticketDocument.getChildId(), ticketDocument.getTicketTypeId())
                                .flatMap(tuple -> mapToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2()))
                );
    }
}
