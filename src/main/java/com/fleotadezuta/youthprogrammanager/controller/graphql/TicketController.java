package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.TicketChildTicketTypeFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.HistoryData;
import graphql.GraphQLContext;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class TicketController {
    private final TicketChildTicketTypeFacade ticketChildTicketTypeFacade;

    @QueryMapping("getAllTickets")
    public Flux<TicketDto> getAllTickets(Authentication authentication, GraphQLContext context) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt principal) {
            List<String> permissions = principal.getClaimAsStringList("permissions");
            System.out.println("Permissions: " + permissions);
            if (permissions.contains("list:tickets")) {
                return ticketChildTicketTypeFacade.getAllTickets(new UserDetails(context))
                        .doOnComplete(() -> log.info("All ticket fetched successfully"));
            } else {
                return Flux.error(new IllegalArgumentException("User not authorized"));
            }
        } else {
            return Flux.error(new IllegalArgumentException("User not authenticated"));
        }

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

    @MutationMapping("deleteTicket")
    public Mono<TicketDto> deleteTicket(@Argument String id) {
        return ticketChildTicketTypeFacade.deleteTicket(id)
                .doOnSuccess(deletedTicket -> log.info("Deleted Ticket with ID: " + deletedTicket.getId()));
    }

    @QueryMapping("getPotentialTicketTypes")
    public Flux<TicketTypeDto> getPotentialTicketTypes(@Argument String name) {
        return ticketChildTicketTypeFacade.getPotentialTicketTypes(name)
                .doOnNext(ticket -> log.info("Ticket type with name " + ticket.getName() + " fetched successfully"));
    }

    @MutationMapping("reportParticipation")
    public Mono<TicketDto> reportParticipation(@Argument String id, @Argument @RequestBody HistoryData historyData) {
        return ticketChildTicketTypeFacade.reportParticipation(id, historyData)
                .doOnSuccess(deletedTicket -> log.info("Participation reported with ID: " + deletedTicket.getId()));
    }

    @MutationMapping("removeParticipation")
    public Mono<TicketDto> removeParticipation(@Argument String id, @Argument @RequestBody HistoryData historyData) {
        return ticketChildTicketTypeFacade.removeParticipation(id, historyData)
                .doOnSuccess(deletedTicket -> log.info("Participation reported with ID: " + deletedTicket.getId()));

    }

}

