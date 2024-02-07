package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.HistoryData;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.TicketService;
import com.fleotadezuta.youthprogrammanager.service.TicketTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class TicketChildTicketTypeFacade {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketTypeService ticketTypeService;
    private final ChildService childService;
    private final ChildMapper childMapper;

    public Flux<TicketTypeDto> getPotentialTicketTypes(String name) {
        return ticketTypeService.findByName(name);
    }

    public Flux<TicketDto> getAllTickets() {
        return ticketService.findAll()
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> getTicketById(String id) {
        return ticketService.findById(id)
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> addTicket(TicketCreationDto ticketCreationDto) {
        return Mono.just(ticketCreationDto)
                .map(ticketMapper::fromTicketCreationDtoToTicketDocument)
                .map(ticketDocument -> {
                    ticketDocument.setHistoryLog(new ArrayList<>());
                    return ticketDocument;
                })
                .flatMap(ticketService::save)
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> deletedTicket(String id) {
        return ticketService.findById(id)
                .flatMap(ticket -> ticketService.deleteById(id).then(Mono.just(ticket)))
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> updateTicket(String id, TicketUpdateDto ticketUpdateDto) {
        return ticketService.findById(id)
                .map(ticketDocument -> ticketMapper
                        .fromTicketUpdateDtoToTicketDocument(
                                ticketUpdateDto, ticketDocument.getHistoryLog())
                )
                .flatMap(ticketDoc -> {
                    ticketDoc.setId(id);
                    return ticketService.save(ticketDoc);
                })
                .flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }


    public Mono<TicketDto> reportParticipation(String id, HistoryData historyData) {
        return ticketService.findById(id)
                .flatMap(ticketDocument -> {
                    ticketDocument.getHistoryLog().add(historyData);
                    return ticketService.save(ticketDocument);
                }).flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> removeParticipation(String id, HistoryData historyData) {
        return ticketService.findById(id)
                .flatMap(ticketDocument -> {
                    ticketDocument.getHistoryLog().remove(historyData);
                    return ticketService.save(ticketDocument);
                }).flatMap(ticketDoc ->
                        getChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    private Mono<TicketDto> getChildAndTicketType(TicketDocument ticketDocument, String childId, String ticketTypeId) {
        return Mono.zip(
                childService.findById(childId).map(childMapper::fromChildDocumentToChildDto),
                ticketTypeService.findById(ticketTypeId)
        ).flatMap(tuple -> Mono.just(ticketMapper
                .fromTicketDocumentToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2())));
    }

}
