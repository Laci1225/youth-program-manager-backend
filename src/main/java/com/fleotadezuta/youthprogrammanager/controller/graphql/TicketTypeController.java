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

    @QueryMapping("getAllTicketTypes")
    public Flux<TicketTypeDto> getAllTicketTypes() {
        return ticketTypeService.getAllTicketTypes()
                .doOnNext(ticketTypeDto -> log.info(ticketTypeDto.getName()));
        //.doOnComplete(() -> log.info("All ticket types fetched successfully"));
    }

    @QueryMapping("getTicketTypeById")
    public Mono<TicketTypeDto> getTicketTypeById(@Argument String id) {
        return ticketTypeService.getTicketTypeById(id)
                .doOnSuccess(ticketTypeDto -> log.info("Retrieved Ticket type by ID: " + id));
    }

    @MutationMapping("addTicketType")
    public Mono<TicketTypeDto> addTicketType(@Valid @RequestBody @Argument TicketTypeDto ticket) {
        return ticketTypeService.addTicketType(ticket)
                .doOnSuccess(ticketTypeDto -> log.info("Added Ticket type with data: " + ticketTypeDto));
    }

    @MutationMapping("updateTicketType")
    public Mono<TicketTypeDto> updateTicketType(@Argument String id, @Valid @RequestBody @Argument TicketTypeDto ticket) {
        return ticketTypeService.updateTicketType(id, ticket)
                .doOnSuccess(ticketTypeDto -> log.info("Updated Ticket type with ID: " + id));
    }

    @MutationMapping("deletedTicketType")
    public Mono<TicketTypeDto> deletedTicketType(@Argument String id) {
        return ticketTypeService.deletedTicketType(id)
                .doOnSuccess(deletedTicketType -> log.info("Deleted Ticket type with ID: " + deletedTicketType.getId()));
    }

}
