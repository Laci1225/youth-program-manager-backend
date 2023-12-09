package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.service.TicketTypeService;
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
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @QueryMapping("getAllTicketsType")
    public Flux<TicketTypeDto> getAllTickets() {
        return ticketTypeService.getAllTickets()
                .doOnComplete(() -> log.info("All tickets fetched successfully"));
    }

    @QueryMapping("getTicketTypeById")
    public Mono<TicketTypeDto> getTicketById(@Argument String id) {
        return ticketTypeService.getTicketById(id)
                .doOnSuccess(ticketTypeDto -> log.info("Retrieved Ticket by ID: " + id));
    }

    @MutationMapping("addTicketType")
    public Mono<TicketTypeDto> addTicket(@Valid @RequestBody @Argument TicketTypeDto ticket) {
        return ticketTypeService.addTicket(ticket)
                .doOnSuccess(ticketTypeDto -> log.info("Added Ticket with data: " + ticketTypeDto));
    }

    @MutationMapping("updateTicketType")
    public Mono<TicketTypeDto> updateTicket(@Argument String id, @Valid @RequestBody @Argument TicketTypeDto ticket) {
        return ticketTypeService.updateTicket(id, ticket)
                .doOnSuccess(ticketTypeDto -> log.info("Updated Ticket with ID: " + id));
    }

    @MutationMapping("deleteTicketType")
    public Mono<TicketTypeDto> deleteTicket(@Argument String id) {
        return ticketTypeService.deleteTicket(id)
                .doOnSuccess(deletedTicket -> log.info("Deleted Ticket with ID: " + deletedTicket.getId()));
    }

}
