package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.service.TicketService;
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

    private final TicketService ticketService;

    @QueryMapping("getAllTickets")
    public Flux<TicketDto> getAllTickets() {
        return ticketService.getAllTickets()
                .doOnComplete(() -> log.info("All tickets fetched successfully"));
    }

    @QueryMapping("getTicketById")
    public Mono<TicketDto> getTicketById(@Argument String id) {
        return ticketService.getTicketById(id)
                .doOnSuccess(ticketDto -> log.info("Retrieved Ticket by ID: " + id));
    }

    @MutationMapping("addTicket")
    public Mono<TicketDto> addTicket(@Valid @RequestBody @Argument TicketDto ticket) {
        return ticketService.addTicket(ticket)
                .doOnSuccess(ticketDto -> log.info("Added Ticket with data: " + ticketDto));
    }

    @MutationMapping("updateTicket")
    public Mono<TicketDto> updateTicket(@Argument String id, @Valid @RequestBody @Argument TicketDto ticket) {
        return ticketService.updateTicket(id, ticket)
                .doOnSuccess(ticketDto -> log.info("Updated Ticket with ID: " + id));
    }

    @MutationMapping("deleteTicket")
    public Mono<TicketDto> deleteTicket(@Argument String id) {
        return ticketService.deleteTicket(id)
                .doOnSuccess(deletedTicket -> log.info("Deleted Ticket with ID: " + deletedTicket.getId()));
    }

}
