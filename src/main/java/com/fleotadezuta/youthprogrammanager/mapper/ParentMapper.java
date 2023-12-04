package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParentMapper {
    ParentDto fromParentDocumentToParentDto(ParentDocument parent);

    ParentDocument fromParentDtoToParentDocument(ParentDto parentDto);
}
