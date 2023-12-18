package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ParentWithContactDto {
    private String parentId;
    private String familyName;
    private String givenName;
    private List<String> phoneNumbers;
    private String address;
    private Boolean isEmergencyContact;
}
