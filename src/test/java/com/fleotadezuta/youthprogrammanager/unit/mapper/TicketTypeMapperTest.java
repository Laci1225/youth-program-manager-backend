package com.fleotadezuta.youthprogrammanager.unit.mapper;

import com.fleotadezuta.youthprogrammanager.fixtures.service.TicketTypeFixture;
import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TicketTypeMapperImpl.class)
public class TicketTypeMapperTest {

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    @Test
    void fromTicketTypeDocumentToTicketTypeDto() {
        var ticketTypeDto = ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(TicketTypeFixture.getTicketTypeDocument());
        assertEquals(ticketTypeDto, TicketTypeFixture.getTicketTypeDto());
    }

    @Test
    void fromTicketTypeDtoToTicketTypeDocument() {
        var ticketTypeDocument = ticketTypeMapper.fromTicketTypeDtoToTicketTypeDocument(TicketTypeFixture.getTicketTypeDto());
        assertEquals(ticketTypeDocument, TicketTypeFixture.getTicketTypeDocument());
    }

    @Test
    void fromTicketTypeDocumentToTicketTypeDto_NullInput_ReturnsNull() {
        var result = ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(null);
        assertNull(result);
    }

    @Test
    void fromTicketTypeDtoToTicketTypeDocument_NullInput_ReturnsNull() {
        var result = ticketTypeMapper.fromTicketTypeDtoToTicketTypeDocument(null);
        assertNull(result);
    }
}
