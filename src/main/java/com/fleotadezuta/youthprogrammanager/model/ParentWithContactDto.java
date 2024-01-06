package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ParentWithContactDto {
    private ParentDto parentDto;
    private Boolean isEmergencyContact;
}
