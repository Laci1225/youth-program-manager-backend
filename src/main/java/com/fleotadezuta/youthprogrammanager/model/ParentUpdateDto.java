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
public class ParentUpdateDto {
    private String id;
    private String email;
    private String familyName;
    private String givenName;
    private List<String> phoneNumbers;
    private List<String> childIds;
    private String address;
}
