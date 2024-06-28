package com.fleotadezuta.youthprogrammanager.unit.mapper;

import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.mapper.*;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.ChildWithParentsDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ChildMapperImpl.class, DiseaseMapperImpl.class, MedicineMapperImpl.class})
public class ChildMapperTest {

    @Autowired
    private ChildMapper childMapper;

    @Test
    void fromChildDocumentToChildDto() {
        var childDocument = childMapper
                .fromChildDtoToChildDocument(ChildFixture.getChildDto());

        assertEquals(childDocument, ChildFixture.getChildDocument());
    }

    @Test
    void fromChildDtoToChildDocument() {
        var childDto = childMapper
                .fromChildDocumentToChildDto(ChildFixture.getChildDocument());

        assertEquals(childDto, ChildFixture.getChildDto());
    }

    @Test
    void fromChildCreationDtoToChildDocument() {
        var childDocument = childMapper
                .fromChildCreationDtoToChildDocument(ChildFixture.getChildCreateDto());
        childDocument.setId(ChildFixture.getChildDocument().getId());

        assertEquals(childDocument, ChildFixture.getChildDocument());
    }

    @Test
    void fromChildUpdateDtoToChildDocument() {
        var childDocument = childMapper
                .fromChildUpdateDtoToChildDocument(ChildFixture.getChildUpdateDto());

        assertEquals(childDocument, ChildFixture.getChildDocument());
    }

    @Test
    void fromChildDocumentToChildUpdateDto() {
        var childUpdateDto = childMapper
                .fromChildDocumentToChildUpdateDto(ChildFixture.getChildDocument());

        assertEquals(childUpdateDto, ChildFixture.getChildUpdateDto());
    }

    @Test
    void fromChildDtoToChildWithParentsDocument() {
        var childWithParentsDto = childMapper
                .fromChildDtoToChildWithParentsDocument(ChildFixture.getChildDocument());
        childWithParentsDto.setParents(ChildFixture.getChildWithParentsDto().getParents());

        assertEquals(childWithParentsDto, ChildFixture.getChildWithParentsDto());
    }

    @Test
    void fromChildDtoToChildDocument_NullInput_ReturnsNull() {
        ChildDocument result = childMapper.fromChildDtoToChildDocument(null);
        assertNull(result);
    }

    @Test
    void fromChildDocumentToChildDto_NullInput_ReturnsNull() {
        ChildDto result = childMapper.fromChildDocumentToChildDto(null);
        assertNull(result);
    }

    @Test
    void fromChildCreationDtoToChildDocument_NullInput_ReturnsNull() {
        ChildDocument result = childMapper.fromChildCreationDtoToChildDocument(null);
        assertNull(result);
    }

    @Test
    void fromChildUpdateDtoToChildDocument_NullInput_ReturnsNull() {
        ChildDocument result = childMapper.fromChildUpdateDtoToChildDocument(null);
        assertNull(result);
    }

    @Test
    void fromChildDocumentToChildUpdateDto_NullInput_ReturnsNull() {
        ChildUpdateDto result = childMapper.fromChildDocumentToChildUpdateDto(null);
        assertNull(result);
    }

    @Test
    void fromChildDtoToChildWithParentsDocument_NullInput_ReturnsNull() {
        ChildWithParentsDto result = childMapper.fromChildDtoToChildWithParentsDocument(null);
        assertNull(result);
    }
}
