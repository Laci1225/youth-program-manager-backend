package com.fleotadezuta.youthprogrammanager.model;

import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeType;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmployeeDto {
    private String id;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    private Role type;
}
