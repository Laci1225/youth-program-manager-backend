package com.fleotadezuta.youthprogrammanager.persistance.document;

import com.fleotadezuta.youthprogrammanager.constants.Diseases;
import com.fleotadezuta.youthprogrammanager.constants.Medicines;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    private String birthDate;
    private String birthPlace;
    private String address;
    private Diseases diseases;
    private Medicines medicines;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime modifiedDate;
    //TODO
}

