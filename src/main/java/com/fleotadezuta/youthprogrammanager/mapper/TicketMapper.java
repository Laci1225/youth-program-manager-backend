package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.HistoryData;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import org.mapstruct.*;

import java.util.List;

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

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "historyLog", ignore = true)
    TicketDocument fromTicketUpdateDtoToTicketDocument(TicketUpdateDto ticketUpdateDto,
                                                       @Context List<HistoryData> historyData);

    @AfterMapping
    default void fromTicketUpdateDtoToTicketDocument(@MappingTarget TicketDocument ticketDocument,
                                                     @Context List<HistoryData> historyData) {
        ticketDocument.setHistoryLog(historyData);
    }

    TicketDocument fromTicketCreationDtoToTicketDocument(TicketCreationDto ticketCreationDto);
}
