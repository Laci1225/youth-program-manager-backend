package com.fleotadezuta.youthprogrammanager.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "children")
public class ChildDocument {
    @Id
    private String id;
    private String familyName;
    private String givenName;
    private LocalDateTime birthDate;
    private String birthPlace;
    private String address;
    private List<RelativeParent> relativeParents;
    private List<Disease> diagnosedDiseases;
    private List<Medicine> regularMedicines;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime modifiedDate;

}

