package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.HistoryData;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TicketFixture {

    private static final String CHILD_ID = "child123";
    private static final String TICKET_TYPE_ID = "type123";
    private static final LocalDateTime ISSUE_DATE = LocalDateTime.now().minusDays(10);
    private static final LocalDateTime EXPIRATION_DATE = ISSUE_DATE.plusMonths(1);
    private static final Integer PRICE = 50;
    private static final Integer NUMBER_OF_PARTICIPATION = 5;
    private static final List<HistoryData> HISTORY_LOG = List.of(
            new HistoryData(LocalDateTime.now().minusDays(5), "Event1"),
            new HistoryData(LocalDateTime.now().minusDays(2), "Event2")
    );

    public static TicketDto getTicketDto() {
        return TicketDto.builder()
                .id("ticket123")
                .ticketType(TicketTypeFixture.getTicketTypeDto())
                .issueDate(ISSUE_DATE)
                .expirationDate(EXPIRATION_DATE)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .historyLog(HISTORY_LOG)
                .build();
    }

    public static TicketDto getTicketDtoWithoutId() {
        return TicketDto.builder()
                .ticketType(TicketTypeFixture.getTicketTypeDto())
                .issueDate(ISSUE_DATE)
                .expirationDate(EXPIRATION_DATE)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .historyLog(HISTORY_LOG)
                .build();
    }

    public static TicketDocument getTicketDocument() {
        return TicketDocument.builder()
                .id("ticket123")
                .childId(CHILD_ID)
                .ticketTypeId(TICKET_TYPE_ID)
                .issueDate(ISSUE_DATE)
                .expirationDate(EXPIRATION_DATE)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .historyLog(HISTORY_LOG)
                .build();
    }

    public static TicketDocument getTicketDocumentWithoutId() {
        return TicketDocument.builder()
                .childId(CHILD_ID)
                .ticketTypeId(TICKET_TYPE_ID)
                .issueDate(ISSUE_DATE)
                .expirationDate(EXPIRATION_DATE)
                .price(PRICE)
                .numberOfParticipation(NUMBER_OF_PARTICIPATION)
                .historyLog(HISTORY_LOG)
                .build();
    }

    public static List<TicketDto> getTicketDtoList() {
        var list = new ArrayList<TicketDto>();
        list.add(getTicketDto());
        return list;
    }

    public static List<TicketDocument> getTicketDocumentList() {
        var list = new ArrayList<TicketDocument>();
        list.add(getTicketDocument());
        return list;
    }
}
