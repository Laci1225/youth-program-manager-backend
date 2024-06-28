package com.fleotadezuta.youthprogrammanager.unit.mapper;


import com.fleotadezuta.youthprogrammanager.fixtures.service.ParentFixture;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ParentMapperImpl.class)
public class ParentMapperTest {

    @Autowired
    private ParentMapper parentMapper;

    @Test
    void fromParentDocumentToParentDto() {
        var parentDto = parentMapper.fromParentDocumentToParentDto(ParentFixture.getParentDocument());
        assertEquals(parentDto, ParentFixture.getParentDto());
    }

    @Test
    void fromParentDocumentToParentUpdateDto() {
        var parentUpdateDto = parentMapper.fromParentDocumentToParentUpdateDto(ParentFixture.getParentDocument());
        parentUpdateDto.setChildIds(ParentFixture.getParentUpdateDto().getChildIds());
        assertEquals(parentUpdateDto, ParentFixture.getParentUpdateDto());
    }

    @Test
    void fromParentDtoToParentDocument() {
        var parentDocument = parentMapper.fromParentDtoToParentDocument(ParentFixture.getParentDto());
        assertEquals(parentDocument, ParentFixture.getParentDocument());
    }

    @Test
    void fromParentDocumentToParentWithChildrenDto() {
        var parentWithChildrenDto = parentMapper.fromParentDocumentToParentWithChildrenDto(ParentFixture.getParentDocument());
        parentWithChildrenDto.setChildDtos(ParentFixture.getParentWithChildrenDto().getChildDtos());
        assertEquals(parentWithChildrenDto, ParentFixture.getParentWithChildrenDto());
    }

    @Test
    void fromParentUpdateDtoToParentDto() {
        var parentDto = parentMapper.fromParentUpdateDtoToParentDto(ParentFixture.getParentUpdateDto());
        assertEquals(parentDto, ParentFixture.getParentDto());
    }

    @Test
    void fromParentCreateDtoToParentDto() {
        var parentDto = parentMapper.fromParentCreateDtoToParentDto(ParentFixture.getParentCreateDto());
        parentDto.setId(ParentFixture.getParentDto().getId());
        assertEquals(parentDto, ParentFixture.getParentDto());
    }

    @Test
    void fromParentDocumentToParentDto_NullInput_ReturnsNull() {
        var result = parentMapper.fromParentDocumentToParentDto(null);
        assertNull(result);
    }

    @Test
    void fromParentDocumentToParentUpdateDto_NullInput_ReturnsNull() {
        var result = parentMapper.fromParentDocumentToParentUpdateDto(null);
        assertNull(result);
    }

    @Test
    void fromParentDtoToParentDocument_NullInput_ReturnsNull() {
        var result = parentMapper.fromParentDtoToParentDocument(null);
        assertNull(result);
    }

    @Test
    void fromParentDocumentToParentWithChildrenDto_NullInput_ReturnsNull() {
        var result = parentMapper.fromParentDocumentToParentWithChildrenDto(null);
        assertNull(result);
    }

    @Test
    void fromParentUpdateDtoToParentDto_NullInput_ReturnsNull() {
        var result = parentMapper.fromParentUpdateDtoToParentDto(null);
        assertNull(result);
    }

    @Test
    void fromParentCreateDtoToParentDto_NullInput_ReturnsNull() {
        var result = parentMapper.fromParentCreateDtoToParentDto(null);
        assertNull(result);
    }
}

