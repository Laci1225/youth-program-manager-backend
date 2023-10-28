package com.fleotadezuta.youthprogrammanager.persistance.document;

import com.fleotadezuta.youthprogrammanager.constants.Disease;
import com.fleotadezuta.youthprogrammanager.constants.Medicine;
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
    private List<Disease> diagnosedDiseases; //TODO id for disease for removal?
    private List<Medicine> regularMedicines; //TODO id for medicine for removal?
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime modifiedDate;

}

