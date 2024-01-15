package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "ticketType", ignore = true)
    TicketDto fromTicketDocumentToTicketDto(TicketDocument ticketDocument);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "ticketType", ignore = true)
    TicketDto fromTicketDocumentToTicketDto(TicketDocument ticketDocument,
                                            @Context ChildDto childDto,
                                            @Context TicketTypeDto ticketTypeDto);

    @AfterMapping
    default void fromTicketDocumentToTicketDto(@MappingTarget TicketDto ticketDto,
                                               @Context ChildDto childDto,
                                               @Context TicketTypeDto ticketTypeDto) {
        ticketDto.setChild(childDto);
        ticketDto.setTicketType(ticketTypeDto);
    }

    @Mapping(target = "childId", source = "ticketDto.child.id")
    @Mapping(target = "ticketTypeId", source = "ticketDto.ticketType.id")
    TicketDocument fromTicketDtoToTicketDocument(TicketDto ticketDto);

    @Mapping(target = "childId", source = "ticketDto.child.id")
    @Mapping(target = "ticketTypeId", source = "ticketDto.ticketType.id")
    TicketDocument fromTicketUpdateDtoToTicketDocument(TicketUpdateDto ticketUpdateDto);

    @Mapping(target = "childId", source = "ticketDto.child.id")
    @Mapping(target = "ticketTypeId", source = "ticketDto.ticketType.id")
    TicketDocument fromTicketCreationDtoToTicketDocument(TicketCreationDto ticketCreationDto);
}
