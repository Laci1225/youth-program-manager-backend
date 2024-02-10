package com.fleotadezuta.youthprogrammanager.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Data
@Builder
@Document
public class RelativeParent {
    @Id
    private String id;
    private Boolean isEmergencyContact;
}
