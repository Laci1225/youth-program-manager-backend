package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.ChildCreateDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.ChildWithParentsDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.Disease;
import com.fleotadezuta.youthprogrammanager.persistence.document.Medicine;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DiseaseMapper.class, MedicineMapper.class, RelativeParentsMapper.class})
public interface ChildMapper {

    @Mapping(target = "hasDiagnosedDiseases", source = "childDocument.diagnosedDiseases")
    @Mapping(target = "hasRegularMedicines", source = "childDocument.regularMedicines")
    ChildDto fromChildDocumentToChildDto(ChildDocument childDocument);

    default boolean hasDiagnosedDiseases(List<Disease> diagnosedDisease) {
        return diagnosedDisease != null && !diagnosedDisease.isEmpty();
    }

    default boolean hasRegularMedicines(List<Medicine> regularMedicines) {
        return regularMedicines != null && !regularMedicines.isEmpty();
    }

    @Mapping(target = "relativeParents", source = "childCreateDto.relativeParent")
    ChildDocument fromChildCreationDtoToChildDocument(ChildCreateDto childCreateDto);

    default List<RelativeParent> relativeParents(RelativeParent relativeParent) {
        return List.of(relativeParent);
    }

    ChildDocument fromChildUpdateDtoToChildDocument(ChildUpdateDto childUpdateDto);

    @Mapping(target = "hasDiagnosedDiseases", source = "childDocument.diagnosedDiseases")
    @Mapping(target = "hasRegularMedicines", source = "childDocument.regularMedicines")
    ChildUpdateDto fromChildDocumentToChildUpdateDto(ChildDocument childDocument);

    @Mapping(target = "hasDiagnosedDiseases", source = "childDocument.diagnosedDiseases")
    @Mapping(target = "hasRegularMedicines", source = "childDocument.regularMedicines")
    ChildWithParentsDto fromChildDtoToChildWithParentsDocument(ChildDocument childDocument);
}
