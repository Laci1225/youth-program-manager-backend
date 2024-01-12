package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketDto fromTicketDocumentToTicketDto(TicketDocument ticketDocument);

    TicketDocument fromTicketDtoToTicketDocument(TicketDto ticketDto);
}
