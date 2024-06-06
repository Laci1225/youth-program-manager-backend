package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketTypeFixture {

    private static final String ID = "type123";
    private static final String NAME = "Standard Ticket";
    private static final String DESCRIPTION = "A standard ticket for general use";
    private static final Double PRICE = 20.0;
    private static final Integer NUMBER_OF_PARTICIPATION = 10;
    private static final Integer STANDARD_VALIDITY_PERIOD = 30;

    public static TicketTypeDto getTicketTypeDto() {
        return TicketTypeDto.builder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .standardValidityPeriod(STANDARD_VALIDITY_PERIOD)
                .build();
    }

    public static TicketTypeDto getTicketTypeDtoWithoutId() {
        return TicketTypeDto.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .standardValidityPeriod(STANDARD_VALIDITY_PERIOD)
                .build();
    }

    public static TicketTypeDocument getTicketTypeDocument(boolean withId) {
        var ticketTypeDocument = TicketTypeDocument.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .standardValidityPeriod(STANDARD_VALIDITY_PERIOD)
                .build();
        if (withId) {
            ticketTypeDocument.setId(ID);
        }
        return ticketTypeDocument;
    }

    public static TicketTypeDocument getTicketTypeDocument(String id) {
        return TicketTypeDocument.builder()
                .id(id)
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .standardValidityPeriod(STANDARD_VALIDITY_PERIOD)
                .build();
    }

    public static List<TicketTypeDto> getTicketTypeDtoList() {
        var list = new ArrayList<TicketTypeDto>();
        list.add(getTicketTypeDto());
        return list;
    }

    public static List<TicketTypeDocument> getTicketTypeDocumentList() {
        var list = new ArrayList<TicketTypeDocument>();
        list.add(getTicketTypeDocument(true));
        return list;
    }
}
