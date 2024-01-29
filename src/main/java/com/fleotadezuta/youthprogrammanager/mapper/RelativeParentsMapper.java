package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.RelativeParentsDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelativeParentsMapper {
    RelativeParentsDto fromRelativeParentsToRelativeParentsDto(RelativeParent parents);

    RelativeParent fromRelativeParentsDtoToRelativeParents(RelativeParentsDto parentDto);
}
