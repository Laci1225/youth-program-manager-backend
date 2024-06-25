package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.EmployeeUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;

import java.util.Arrays;
import java.util.List;

public class EmployeeFixture {

    private static final String EMPLOYEE_ID = "1234";
    private static final String EMPLOYEE_ID_2 = "5678";
    private static final String GIVEN_NAME = "John";
    private static final String FAMILY_NAME = "Doe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String PHONE_NUMBER = "1234567890";
    private static final Role ROLE = Role.ADMINISTRATOR;

    private static final String GIVEN_NAME_2 = "Jane";
    private static final String FAMILY_NAME_2 = "Smith";
    private static final String EMAIL_2 = "jane.smith@example.com";
    private static final String PHONE_NUMBER_2 = "0987654321";
    private static final Role ROLE_2 = Role.PARENT;

    public static List<EmployeeDocument> getEmployeeDocumentList() {
        return Arrays.asList(
                EmployeeDocument.builder()
                        .id(EMPLOYEE_ID)
                        .givenName(GIVEN_NAME)
                        .familyName(FAMILY_NAME)
                        .email(EMAIL)
                        .phoneNumber(PHONE_NUMBER)
                        .type(ROLE)
                        .build(),
                EmployeeDocument.builder()
                        .id(EMPLOYEE_ID_2)
                        .givenName(GIVEN_NAME_2)
                        .familyName(FAMILY_NAME_2)
                        .email(EMAIL_2)
                        .phoneNumber(PHONE_NUMBER_2)
                        .type(ROLE_2)
                        .build()
        );
    }

    public static List<EmployeeDto> getEmployeeDtoList() {
        return Arrays.asList(
                EmployeeDto.builder()
                        .id(EMPLOYEE_ID)
                        .givenName(GIVEN_NAME)
                        .familyName(FAMILY_NAME)
                        .email(EMAIL)
                        .phoneNumber(PHONE_NUMBER)
                        .type(ROLE)
                        .build(),
                EmployeeDto.builder()
                        .id(EMPLOYEE_ID_2)
                        .givenName(GIVEN_NAME_2)
                        .familyName(FAMILY_NAME_2)
                        .email(EMAIL_2)
                        .phoneNumber(PHONE_NUMBER_2)
                        .type(ROLE_2)
                        .build()
        );
    }

    public static EmployeeDocument getEmployeeDocument() {
        return EmployeeDocument.builder()
                .id(EMPLOYEE_ID)
                .givenName(GIVEN_NAME)
                .familyName(FAMILY_NAME)
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .type(ROLE)
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
                .build();
    }

    public static EmployeeUpdateDto getEmployeeUpdateDto() {
        return EmployeeUpdateDto.builder()
                .id(EMPLOYEE_ID)
                .givenName(GIVEN_NAME)
                .familyName(FAMILY_NAME)
                .email(EMAIL)
                .phoneNumber(PHONE_NUMBER)
                .build();
    }

}
