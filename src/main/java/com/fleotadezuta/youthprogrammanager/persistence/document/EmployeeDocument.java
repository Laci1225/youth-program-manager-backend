package com.fleotadezuta.youthprogrammanager.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "employees")
public class EmployeeDocument {
    private String id;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    private Role type;
}
