package com.fleotadezuta.youthprogrammanager.persistance.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "children")
public class ChildDocument {
    @Id
    private String id;
    private String FirstName;
    private String FamilyName;
    private String email;
    private String phoneNumber;
}
