package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmployeeUpdateDto {
    private String id;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
