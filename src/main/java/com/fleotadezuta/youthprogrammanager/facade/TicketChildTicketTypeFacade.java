package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.HistoryData;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.TicketService;
import com.fleotadezuta.youthprogrammanager.service.TicketTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public Flux<TicketTypeDto> getPotentialTicketTypes(String name) {
        return ticketTypeService.findByName(name);
    }

    public Flux<TicketDto> getAllTickets(UserDetails userDetails) {
        if (userDetails.getUserType().equals("ADMIN")) {
            return ticketService.findAll()
                    .map(ticketMapper::fromTicketDtoToTicketDocument)
                    .flatMap(ticketDoc ->
                            getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                    );
        } else {
            return childService.getAllChildren(userDetails)
                    .map(ChildDto::getId)
                    .collectList()
                    .flatMapIterable(ids -> ids)
                    .flatMap(ticketService::findAllByChildId)
                    .map(ticketMapper::fromTicketDtoToTicketDocument)
                    .flatMap(ticketDoc ->
                            getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                    );
        }
    }

    public Mono<TicketDto> getTicketById(String id) {
        return ticketService.findById(id)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketDoc ->
                        getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
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
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketDoc ->
                        getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> deleteTicket(String id) {
        return ticketService.findById(id)
                .flatMap(ticket -> ticketService.deleteById(id)
                        .then(Mono.just(ticket)
                                .map(ticketMapper::fromTicketDtoToTicketDocument)))
                .flatMap(ticketDoc ->
                        getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
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
                    return ticketService.save(ticketDoc)
                            .map(ticketMapper::fromTicketDtoToTicketDocument);
                })
                .flatMap(ticketDoc ->
                        getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }


    public Mono<TicketDto> reportParticipation(UserDetails userDetails, String id, HistoryData historyData) {
        return ticketService.findById(id)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketDocument -> {
                    ticketDocument.getHistoryLog().add(historyData);
                    return ticketService.save(ticketDocument)
                            .map(ticketMapper::fromTicketDtoToTicketDocument);
                }).flatMap(ticketDoc ->
                        getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    public Mono<TicketDto> removeParticipation(UserDetails userDetails, String id, HistoryData historyData) {
        if (!userDetails.getUserType().equals("ADMIN")) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to report participation"));
        }
        return ticketService.findById(id)
                .map(ticketMapper::fromTicketDtoToTicketDocument)
                .flatMap(ticketDocument -> {
                    ticketDocument.getHistoryLog().remove(historyData);
                    return ticketService.save(ticketDocument)
                            .map(ticketMapper::fromTicketDtoToTicketDocument);
                }).flatMap(ticketDoc ->
                        getTicketDtoWithChildAndTicketType(ticketDoc, ticketDoc.getChildId(), ticketDoc.getTicketTypeId())
                );
    }

    private Mono<TicketDto> getTicketDtoWithChildAndTicketType(TicketDocument ticketDocument, String childId, String ticketTypeId) {
        return Mono.zip(
                childService.findById(childId),
                ticketTypeService.findById(ticketTypeId)
        ).flatMap(tuple -> Mono.just(ticketMapper
                .fromTicketDocumentToTicketDto(ticketDocument, tuple.getT1(), tuple.getT2())));
    }

}
