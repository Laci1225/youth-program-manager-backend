package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.RelativeParentsDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParents;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelativeParentsMapper {
    RelativeParentsDto fromRelativeParentsToRelativeParentsDto(RelativeParents parents);

    RelativeParents fromRelativeParentsDtoToRelativeParents(RelativeParentsDto parentDto);
}
