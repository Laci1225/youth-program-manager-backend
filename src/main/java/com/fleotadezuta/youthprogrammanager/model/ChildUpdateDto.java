package com.fleotadezuta.youthprogrammanager.model;

import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChildUpdateDto {
    private String id;
    private String familyName;
    private String givenName;
    private LocalDateTime birthDate;
    private String birthPlace;
    private String address;
    private List<RelativeParent> relativeParents;
    private List<DiseaseDto> diagnosedDiseases;
    private List<MedicineDto> regularMedicines;
    private Boolean hasDiagnosedDiseases;
    private Boolean hasRegularMedicines;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}