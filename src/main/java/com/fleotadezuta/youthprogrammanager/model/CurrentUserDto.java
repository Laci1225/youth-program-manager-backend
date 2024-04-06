package com.fleotadezuta.youthprogrammanager.model;

import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserDto {
    Role userType;
    String userId;
}
