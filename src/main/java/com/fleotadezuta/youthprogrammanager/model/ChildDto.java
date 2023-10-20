package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChildDto {
    @Id
    private String id;
    private String familyName;
    private String givenName;
    private String birthDate;
    private String birthPlace;
    private String address;
    //private Diseases diseases;
    //private Medicines medicines;
    //@CreatedDate
    //private LocalDateTime createdDate;
    //@LastModifiedDate
    //private LocalDateTime modifiedDate;
}