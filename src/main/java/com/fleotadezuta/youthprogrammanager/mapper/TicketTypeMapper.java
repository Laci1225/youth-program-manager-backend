package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {
    TicketTypeDto fromTicketTypeDocumentToTicketTypeDto(TicketTypeDocument ticketTypeDocument);

    TicketTypeDocument fromTicketTypeDtoToTicketTypeDocument(TicketTypeDto ticketTypeDto);
}
