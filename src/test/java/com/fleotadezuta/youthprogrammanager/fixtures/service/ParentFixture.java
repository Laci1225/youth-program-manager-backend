package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ParentFixture {

    private static final String ID = "parent123";
    private static final String EMAIL = "parent@example.com";
    private static final String FAMILY_NAME = "Doe";
    private static final String GIVEN_NAME = "John";
    private static final List<String> VALID_PHONE_NUMBERS = List.of("+36203333333", "+36202222222");
    private static final List<String> INVALID_PHONE_NUMBERS = List.of("12345", "invalid-phone");
    private static final String ADDRESS = "123 Main St, Anytown, USA";
    private static final LocalDateTime CREATED_DATE = LocalDateTime.now();
    private static final LocalDateTime MODIFIED_DATE = LocalDateTime.now();

    public static ParentDto getParentDto() {
        return ParentDto.builder()
                .id(ID)
                .email(EMAIL)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .phoneNumbers(VALID_PHONE_NUMBERS)
                .address(ADDRESS)
                .build();
    }

    public static ParentDto getParentDtoWithoutId() {
        return ParentDto.builder()
                .email(EMAIL)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .phoneNumbers(VALID_PHONE_NUMBERS)
                .address(ADDRESS)
                .build();
    }

    public static ParentDto getParentDtoWithValidPhoneNumbers() {
        return ParentDto.builder()
                .id(ID)
                .email(EMAIL)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .phoneNumbers(VALID_PHONE_NUMBERS)
                .address(ADDRESS)
                .build();
    }

    public static ParentDto getParentDtoWithInvalidPhoneNumbers() {
        return ParentDto.builder()
                .id(ID)
                .email(EMAIL)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .phoneNumbers(INVALID_PHONE_NUMBERS)
                .address(ADDRESS)
                .build();
    }

    public static ParentDocument getParentDocument() {
        return ParentDocument.builder()
                .id(ID)
                .email(EMAIL)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .phoneNumbers(VALID_PHONE_NUMBERS)
                .address(ADDRESS)
                .createdDate(CREATED_DATE)
                .modifiedDate(MODIFIED_DATE)
                .build();
    }

    public static ParentDocument getParentDocumentWithoutId() {
        return ParentDocument.builder()
                .email(EMAIL)
                .familyName(FAMILY_NAME)
                .givenName(GIVEN_NAME)
                .phoneNumbers(VALID_PHONE_NUMBERS)
                .address(ADDRESS)
                .createdDate(CREATED_DATE)
                .modifiedDate(MODIFIED_DATE)
                .build();
    }

    public static List<ParentDto> getParentDtoList() {
        var list = new ArrayList<ParentDto>();
        list.add(getParentDto());
        return list;
    }

    public static List<ParentDocument> getParentDocumentList() {
        var list = new ArrayList<ParentDocument>();
        list.add(getParentDocument());
        return list;
    }
}
