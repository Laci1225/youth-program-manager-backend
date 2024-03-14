package com.fleotadezuta.youthprogrammanager.persistence.document;

import lombok.Getter;

@Getter
public enum Role {
    ADMINISTRATOR("rol_ZVbZjai55NpwOwhV"),
    RECEPTIONIST("rol_8zxbgZfLD9gqdT9V"),
    TEACHER("rol_eByb3iqY6qSouy7u"),
    PARENT("rol_Mjt9yu2PlPadWRn5");

    private final String roleId;

    Role(String roleId) {
        this.roleId = roleId;
    }

}

