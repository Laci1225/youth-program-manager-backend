package com.fleotadezuta.youthprogrammanager.unit.mapper;

import com.fleotadezuta.youthprogrammanager.fixtures.service.MedicineFixture;
import com.fleotadezuta.youthprogrammanager.mapper.MedicineMapper;
import com.fleotadezuta.youthprogrammanager.mapper.MedicineMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MedicineMapperImpl.class)
public class MedicineMapperTest {

    @Autowired
    private MedicineMapper medicineMapper;

    @Test
    void fromMedicineToMedicineDto() {
        var medicineDto = medicineMapper.fromMedicineToMedicineDto(MedicineFixture.getMedicine());
        assertEquals(medicineDto, MedicineFixture.getMedicineDto());
    }

    @Test
    void fromMedicineDtoToMedicine() {
        var medicine = medicineMapper.fromMedicineDtoToMedicine(MedicineFixture.getMedicineDto());
        assertEquals(medicine, MedicineFixture.getMedicine());
    }

    @Test
    void fromMedicineToMedicineDto_NullInput_ReturnsNull() {
        var result = medicineMapper.fromMedicineToMedicineDto(null);
        assertNull(result);
    }

    @Test
    void fromMedicineDtoToMedicine_NullInput_ReturnsNull() {
        var result = medicineMapper.fromMedicineDtoToMedicine(null);
        assertNull(result);
    }
}
