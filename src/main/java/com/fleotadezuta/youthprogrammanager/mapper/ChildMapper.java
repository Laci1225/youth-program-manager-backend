package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.persistance.document.ChildDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChildMapper {
    ChildDto fromChildDocumentToChildDto(ChildDocument childDocument);

    ChildDocument fromChildDtoToChildDocument(ChildDto childDto);
}
