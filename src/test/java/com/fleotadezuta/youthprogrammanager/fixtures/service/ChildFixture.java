package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChildFixture {

    private static final String ID = "child123";
    private static final String FAMILY_NAME = "Doe";
    private static final String GIVEN_NAME = "John";
    private static final LocalDateTime BIRTH_DATE = LocalDateTime.of(2010, 5, 15, 0, 0);
    private static final String BIRTH_PLACE = "Anytown, USA";
    private static final String ADDRESS = "123 Main St, Anytown, USA";
    private static final RelativeParent RELATIVE_PARENT = new RelativeParent("parent123", true);
    private static final LocalDateTime CREATED_DATE = LocalDateTime.now();
    private static final LocalDateTime MODIFIED_DATE = LocalDateTime.now();

    public static ChildDocument getChildDocument() {
        return ChildDocument.builder()
                .id(ID)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .birthDate(BIRTH_DATE)
                .birthPlace(BIRTH_PLACE)
                .address(ADDRESS)
                .relativeParents(new ArrayList<>(List.of(RELATIVE_PARENT)))
                .diagnosedDiseases(List.of(DiseaseFixture.getDisease()))
                .regularMedicines(List.of(MedicineFixture.getMedicine()))
                .createdDate(CREATED_DATE)
                .modifiedDate(MODIFIED_DATE)
                .build();
    }

    public static ChildDto getChildDto() {
        return ChildDto.builder()
                .id(ID)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .birthDate(BIRTH_DATE)
                .birthPlace(BIRTH_PLACE)
                .address(ADDRESS)
                .relativeParents(new ArrayList<>(List.of(RELATIVE_PARENT)))
                .diagnosedDiseases(List.of(DiseaseFixture.getDiseaseDto()))
                .regularMedicines(List.of(MedicineFixture.getMedicineDto()))
                .hasDiagnosedDiseases(true)
                .hasRegularMedicines(true)
                .build();
    }

    public static List<ChildDto> getChildDtoList() {
        return List.of(getChildDto());
    }

    public static ChildUpdateDto getChildUpdateDto() {
        return ChildUpdateDto.builder()
                .id(ID)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .birthDate(BIRTH_DATE)
                .birthPlace(BIRTH_PLACE)
                .address(ADDRESS)
                .relativeParents(List.of(RELATIVE_PARENT))
                .diagnosedDiseases(List.of(DiseaseFixture.getDiseaseDto()))
                .regularMedicines(List.of(MedicineFixture.getMedicineDto()))
                .hasDiagnosedDiseases(true)
                .hasRegularMedicines(true)
                .build();
    }


    public static ChildUpdateDto getChildUpdateDtoWithDuplicateParentIds() {
        return ChildUpdateDto.builder()
                .id(ID)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .birthDate(BIRTH_DATE)
                .birthPlace(BIRTH_PLACE)
                .address(ADDRESS)
                .relativeParents(List.of(ParentFixture.getRelativeParent(), ParentFixture.getRelativeParent()))
                .diagnosedDiseases(List.of(DiseaseFixture.getDiseaseDto()))
                .regularMedicines(List.of(MedicineFixture.getMedicineDto()))
                .build();
    }
}
