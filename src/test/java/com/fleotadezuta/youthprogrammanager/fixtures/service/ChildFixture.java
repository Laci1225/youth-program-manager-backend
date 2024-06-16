package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.DiseaseDto;
import com.fleotadezuta.youthprogrammanager.model.MedicineDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.Disease;
import com.fleotadezuta.youthprogrammanager.persistence.document.Medicine;
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
    private static final List<RelativeParent> RELATIVE_PARENTS = getRelativeParents();
    private static final List<Disease> DIAGNOSED_DISEASES = getDiseases();
    private static final List<Medicine> REGULAR_MEDICINES = getMedicines();
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
                .relativeParents(RELATIVE_PARENTS)
                .diagnosedDiseases(DIAGNOSED_DISEASES)
                .regularMedicines(REGULAR_MEDICINES)
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
                .relativeParents(RELATIVE_PARENTS)
                .diagnosedDiseases(List.of(DiseaseFixture.getDiseaseDto()))
                .regularMedicines(List.of(MedicineFixture.getMedicineDto()))
                .build();
    }

    public static ChildUpdateDto getChildUpdateDto() {
        return ChildUpdateDto.builder()
                .id(ID)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .birthDate(BIRTH_DATE)
                .birthPlace(BIRTH_PLACE)
                .address(ADDRESS)
                .relativeParents(RELATIVE_PARENTS)
                .diagnosedDiseases(List.of(DiseaseFixture.getDiseaseDto()))
                .regularMedicines(List.of(MedicineFixture.getMedicineDto()))
                .build();
    }

    public static List<ChildDocument> getChildDocumentList() {
        var list = new ArrayList<ChildDocument>();
        list.add(getChildDocument());
        return list;
    }

    public static List<ChildDto> getChildDtoList() {
        var list = new ArrayList<ChildDto>();
        list.add(getChildDto());
        return list;
    }

    public static List<ChildUpdateDto> getChildUpdateDtoList() {
        var list = new ArrayList<ChildUpdateDto>();
        list.add(getChildUpdateDto());
        return list;
    }

    private static List<RelativeParent> getRelativeParents() {
        var list = new ArrayList<RelativeParent>();
        list.add(ParentFixture.getRelativeParent());
        return list;
    }

    private static List<Disease> getDiseases() {
        var list = new ArrayList<Disease>();
        list.add(DiseaseFixture.getDisease());
        return list;
    }

    private static List<DiseaseDto> getDiseaseDtos() {
        var list = new ArrayList<DiseaseDto>();
        list.add(DiseaseFixture.getDiseaseDto());
        return list;
    }


    private static List<Medicine> getMedicines() {
        var list = new ArrayList<Medicine>();
        list.add(MedicineFixture.getMedicine());
        return list;
    }

    private static List<MedicineDto> getMedicineDtos() {
        var list = new ArrayList<MedicineDto>();
        list.add(MedicineFixture.getMedicineDto());
        return list;
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
