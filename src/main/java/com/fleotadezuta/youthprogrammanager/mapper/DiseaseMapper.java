package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.DiseaseDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.Disease;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiseaseMapper {
    DiseaseDto fromDiseaseToDiseaseDto(Disease disease);

    Disease fromDiseaseDtoToDisease(DiseaseDto diseaseDto);

}
