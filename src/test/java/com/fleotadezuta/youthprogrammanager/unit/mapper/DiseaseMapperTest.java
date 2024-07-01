package com.fleotadezuta.youthprogrammanager.unit.mapper;

import com.fleotadezuta.youthprogrammanager.fixtures.service.DiseaseFixture;
import com.fleotadezuta.youthprogrammanager.mapper.DiseaseMapper;
import com.fleotadezuta.youthprogrammanager.mapper.DiseaseMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DiseaseMapperImpl.class)
public class DiseaseMapperTest {

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Test
    void fromDiseaseToDiseaseDto() {
        var diseaseDto = diseaseMapper.fromDiseaseToDiseaseDto(DiseaseFixture.getDisease());
        assertEquals(diseaseDto, DiseaseFixture.getDiseaseDto());
    }

    @Test
    void fromDiseaseDtoToDisease() {
        var disease = diseaseMapper.fromDiseaseDtoToDisease(DiseaseFixture.getDiseaseDto());
        assertEquals(disease, DiseaseFixture.getDisease());
    }

    @Test
    void fromDiseaseToDiseaseDto_NullInput_ReturnsNull() {
        var result = diseaseMapper.fromDiseaseToDiseaseDto(null);
        assertNull(result);
    }

    @Test
    void fromDiseaseDtoToDisease_NullInput_ReturnsNull() {
        var result = diseaseMapper.fromDiseaseDtoToDisease(null);
        assertNull(result);
    }
}
