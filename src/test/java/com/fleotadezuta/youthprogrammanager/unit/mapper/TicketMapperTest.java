package com.fleotadezuta.youthprogrammanager.unit.mapper;

import com.fleotadezuta.youthprogrammanager.fixtures.service.TicketFixture;
import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketMapperImpl;
import com.fleotadezuta.youthprogrammanager.model.TicketUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TicketMapperImpl.class)
public class TicketMapperTest {

    @Autowired
    private TicketMapper ticketMapper;

    @Test
    void fromTicketDocumentToTicketDto() {
        var ticketDto = ticketMapper.fromTicketDocumentToTicketDto(TicketFixture.getTicketDocument());
        ticketDto.setChild(TicketFixture.getTicketDto().getChild());
        ticketDto.setTicketType(TicketFixture.getTicketDto().getTicketType());
        assertEquals(ticketDto, TicketFixture.getTicketDto());
    }

    @Test
    void fromTicketDocumentToTicketDtoWithContext() {
        var ticketDto = ticketMapper.fromTicketDocumentToTicketDto(
                TicketFixture.getTicketDocument(),
                TicketFixture.getTicketDto().getChild(),
                TicketFixture.getTicketDto().getTicketType()
        );
        assertEquals(ticketDto, TicketFixture.getTicketDto());
    }

    @Test
    void fromTicketDtoToTicketDocument() {
        var ticketDocument = ticketMapper.fromTicketDtoToTicketDocument(TicketFixture.getTicketDto());
        assertEquals(ticketDocument, TicketFixture.getTicketDocument());
    }

    @Test
    void fromTicketUpdateDtoToTicketDocument() {
        var ticketDocument = ticketMapper.fromTicketUpdateDtoToTicketDocument(
                TicketFixture.getTicketUpdateDto(),
                TicketFixture.getTicketDto().getHistoryLog()
        );
        ticketDocument.setId(TicketFixture.getTicketDocument().getId());
        assertEquals(ticketDocument, TicketFixture.getTicketDocument());
    }

    @Test
    void fromTicketCreationDtoToTicketDocument() {
        var ticketDocument = ticketMapper.fromTicketCreationDtoToTicketDocument(
                TicketFixture.getTicketCreationDto()
        );
        ticketDocument.setId(TicketFixture.getTicketDocument().getId());
        ticketDocument.setHistoryLog(TicketFixture.getTicketDocument().getHistoryLog());
        assertEquals(ticketDocument, TicketFixture.getTicketDocument());
    }

    @Test
    void fromTicketDocumentToTicketDto_NullInput_ReturnsNull() {
        var result = ticketMapper.fromTicketDocumentToTicketDto(null);
        assertNull(result);
    }

    @Test
    void fromTicketDocumentToTicketDtoWithContext_NullInput_ReturnsNull() {
        var result = ticketMapper.fromTicketDocumentToTicketDto((TicketDocument) null, null, null);
        assertNull(result);
    }

    @Test
    void fromTicketDtoToTicketDocument_NullInput_ReturnsNull() {
        var result = ticketMapper.fromTicketDtoToTicketDocument(null);
        assertNull(result);
    }

    @Test
    void fromTicketUpdateDDtoToTicketDocument_NullInput_ReturnsNull() {
        var result = ticketMapper.fromTicketUpdateDtoToTicketDocument((TicketUpdateDto) null, null);
        assertNull(result);
    }

    @Test
    void fromTicketCreationDtoToTicketDocument_NullInput_ReturnsNull() {
        var result = ticketMapper.fromTicketCreationDtoToTicketDocument(null);
        assertNull(result);
    }
}
