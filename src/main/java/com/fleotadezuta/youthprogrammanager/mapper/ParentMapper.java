package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.ParentCreateDto;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.model.ParentUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.ParentWithChildrenDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParentMapper {
    ParentDto fromParentDocumentToParentDto(ParentDocument parent);

    ParentUpdateDto fromParentDocumentToParentUpdateDto(ParentDocument parent);

    ParentDocument fromParentDtoToParentDocument(ParentDto parentDto);

    ParentWithChildrenDto fromParentDocumentToParentWithChildrenDto(ParentDocument parent);

    ParentDto fromParentUpdateDtoToParentDto(ParentUpdateDto parentUpdateDto);

    ParentDto fromParentCreateDtoToParentDto(ParentCreateDto parentCreateDto);

}
