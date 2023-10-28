package com.fleotadezuta.youthprogrammanager.model;

import com.fleotadezuta.youthprogrammanager.constants.Disease;
import com.fleotadezuta.youthprogrammanager.constants.Medicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChildDto {
    @Id
    private String id;
    private String familyName;
    private String givenName;
    private LocalDateTime birthDate;
    private String birthPlace;
    private String address;
    private List<Disease> diagnosedDiseases;
    private List<Medicine> regularMedicines;
    private Boolean hasDiagnosedDiseases;
    private Boolean hasRegularMedicines;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime modifiedDate;
}