package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.constants.Disease;
import com.fleotadezuta.youthprogrammanager.constants.Medicine;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.persistance.document.ChildDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChildMapper {

    @Mapping(target = "hasDiagnosedDiseases",source = "childDocument.diagnosedDiseases")
    @Mapping(target = "hasRegularMedicines",source = "childDocument.regularMedicines")
    ChildDto fromChildDocumentToChildDto(ChildDocument childDocument);
    default boolean hasDiagnosedDiseases(List<Disease> diagnosedDisease) {
        return diagnosedDisease!=null && !diagnosedDisease.isEmpty();
    }
    default boolean hasRegularMedicines(List<Medicine> regularMedicines) {
        return regularMedicines!=null && !regularMedicines.isEmpty();
    }

    ChildDocument fromChildDtoToChildDocument(ChildDto childDto);
}
