package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.EmployeeUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;

import java.time.LocalDateTime;
import java.util.List;

public class EmployeeFixture {

    private static final String EMPLOYEE_ID = "1234";
    private static final String GIVEN_NAME = "John";
    private static final String FAMILY_NAME = "Doe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String PHONE_NUMBER = "1234567890";
    private static final Role ROLE = Role.ADMINISTRATOR;
    private static final LocalDateTime CREATED_DATE = LocalDateTime.now();
    private static final LocalDateTime MODIFIED_DATE = LocalDateTime.now();

    public static List<EmployeeDocument> getEmployeeDocumentList() {
        return List.of(getEmployeeDocument());
    }

    public static List<EmployeeDto> getEmployeeDtoList() {
        return List.of(getEmployeeDto());
    }

    public static EmployeeDocument getEmployeeDocument() {
        return EmployeeDocument.builder()
                .id(EMPLOYEE_ID)
                .givenName(GIVEN_NAME)
                .familyName(FAMILY_NAME)
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .type(ROLE)
                .createdDate(CREATED_DATE)
                .modifiedDate(MODIFIED_DATE)
                .build();
    }

    public static EmployeeDto getEmployeeDto() {
        return EmployeeDto.builder()
                .id(EMPLOYEE_ID)
                .givenName(GIVEN_NAME)
                .familyName(FAMILY_NAME)
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .type(ROLE)
                .createdDate(CREATED_DATE)
                .modifiedDate(MODIFIED_DATE)
                .build();
    }

    public static EmployeeUpdateDto getEmployeeUpdateDto() {
        return EmployeeUpdateDto.builder()
                .id(EMPLOYEE_ID)
                .givenName(GIVEN_NAME)
                .familyName(FAMILY_NAME)
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .createdDate(CREATED_DATE)
                .modifiedDate(MODIFIED_DATE)
                .build();
    }

}
