package com.fleotadezuta.youthprogrammanager.constants;

import java.util.List;

import static com.fleotadezuta.youthprogrammanager.persistence.document.Role.*;

public class Employee {
    public static final List<String> EMPLOYEE_USER_TYPES = List.of(ADMINISTRATOR.name(), RECEPTIONIST.name(), TEACHER.name());
}
