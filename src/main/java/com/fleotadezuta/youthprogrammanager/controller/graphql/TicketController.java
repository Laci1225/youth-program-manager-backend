package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.facade.TicketChildTicketTypeFacade;
import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
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
                .doOnNext(ticketDto -> log.info(ticketDto.toString()));
        //.doOnComplete(() -> log.info("All ticket types fetched successfully"));
    }

    @QueryMapping("getTicketById")
    public Mono<TicketDto> getTicketById(@Argument String id) {
        return ticketChildTicketTypeFacade.getTicketById(id)
                .doOnSuccess(ticketDto -> log.info("Retrieved Ticket type by ID: " + id));
    }

    @MutationMapping("addTicket")
    public Mono<TicketDto> addTicket(@Valid @RequestBody @Argument TicketDto ticket) {
        return ticketChildTicketTypeFacade.addTicket(ticket)
                .doOnSuccess(ticketDto -> log.info("Added Ticket type with data: " + ticketDto));
    }

    @MutationMapping("updateTicket")
    public Mono<TicketDto> updateTicket(@Argument String id, @Valid @RequestBody @Argument TicketDto ticket) {
        return ticketChildTicketTypeFacade.updateTicket(id, ticket)
                .doOnSuccess(ticketDto -> log.info("Updated Ticket type with ID: " + id));
    }

    @MutationMapping("deletedTicket")
    public Mono<TicketDto> deletedTicket(@Argument String id) {
        return ticketChildTicketTypeFacade.deletedTicket(id)
                .doOnSuccess(deletedTicket -> log.info("Deleted Ticket type with ID: " + deletedTicket.getId()));
    }

    @QueryMapping("getPotentialTicketTypes")
    public Flux<TicketTypeDto> getPotentialTicketTypes(@Argument String name) {
        return childParentFacade.getPotentialTicketTypes(name)
                .doOnNext(ticket -> log.info("Ticket type with name " + ticket.getName() + " fetched successfully"));
    }

}

