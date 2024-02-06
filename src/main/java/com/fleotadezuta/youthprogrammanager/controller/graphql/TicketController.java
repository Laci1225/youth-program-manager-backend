package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.facade.TicketChildTicketTypeFacade;
import com.fleotadezuta.youthprogrammanager.model.TicketCreationDto;
import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.model.TicketUpdateDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
@Slf4j
public class TicketController {
    private final TicketChildTicketTypeFacade ticketChildTicketTypeFacade;
    private final ChildParentFacade childParentFacade;

    @QueryMapping("getAllTickets")
    public Flux<TicketDto> getAllTickets() {
        return ticketChildTicketTypeFacade.getAllTickets()
                .doOnComplete(() -> log.info("All ticket fetched successfully"));
    }

    @QueryMapping("getTicketById")
    public Mono<TicketDto> getTicketById(@Argument String id) {
        return ticketChildTicketTypeFacade.getTicketById(id)
                .doOnSuccess(ticketDto -> log.info("Retrieved Ticket by ID: " + id));
    }

    @MutationMapping("addTicket")
    public Mono<TicketDto> addTicket(@Valid @RequestBody @Argument TicketCreationDto ticket) {
        return ticketChildTicketTypeFacade.addTicket(ticket)
                .doOnSuccess(ticketDto -> log.info("Added Ticket with data: " + ticketDto));
    }

    @MutationMapping("updateTicket")
    public Mono<TicketDto> updateTicket(@Argument String id, @Valid @RequestBody @Argument TicketUpdateDto ticket) {
        return ticketChildTicketTypeFacade.updateTicket(id, ticket)
                .doOnSuccess(ticketDto -> log.info("Updated Ticket with ID: " + id));
    }

    @MutationMapping("deletedTicket")
    public Mono<TicketDto> deletedTicket(@Argument String id) {
        return ticketChildTicketTypeFacade.deletedTicket(id)
                .doOnSuccess(deletedTicket -> log.info("Deleted Ticket with ID: " + deletedTicket.getId()));
    }

    @QueryMapping("getPotentialTicketTypes")
    public Flux<TicketTypeDto> getPotentialTicketTypes(@Argument String name) {
        return ticketChildTicketTypeFacade.getPotentialTicketTypes(name)
                .doOnNext(ticket -> log.info("Ticket type with name " + ticket.getName() + " fetched successfully"));
    }

}

