package com.fleotadezuta.youthprogrammanager.unit.mapper;

import com.fleotadezuta.youthprogrammanager.fixtures.service.EmployeeFixture;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EmployeeMapperImpl.class)
public class EmployeeMapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    void fromEmployeeDocumentToEmployeeDto() {
        var employeeDto = employeeMapper.fromEmployeeDocumentToEmployeeDto(EmployeeFixture.getEmployeeDocument());
        assertEquals(employeeDto, EmployeeFixture.getEmployeeDto());
    }

    @Test
    void fromEmployeeDtoToEmployeeDocument() {
        var employeeDocument = employeeMapper.fromEmployeeDtoToEmployeeDocument(EmployeeFixture.getEmployeeDto());
        assertEquals(employeeDocument, EmployeeFixture.getEmployeeDocument());
    }

    @Test
    void fromEmployeeUpdateDtoToEmployeeDocument() {
        var employeeDocument = employeeMapper.fromEmployeeUpdateDtoToEmployeeDocument(EmployeeFixture.getEmployeeUpdateDto());
        employeeDocument.setType(EmployeeFixture.getEmployeeDocument().getType());
        assertEquals(employeeDocument, EmployeeFixture.getEmployeeDocument());
    }

    @Test
    void fromEmployeeDocumentToEmployeeDto_NullInput_ReturnsNull() {
        var result = employeeMapper.fromEmployeeDocumentToEmployeeDto(null);
        assertNull(result);
    }

    @Test
    void fromEmployeeDtoToEmployeeDocument_NullInput_ReturnsNull() {
        var result = employeeMapper.fromEmployeeDtoToEmployeeDocument(null);
        assertNull(result);
    }

    @Test
    void fromEmployeeUpdateDtoToEmployeeDocument_NullInput_ReturnsNull() {
        var result = employeeMapper.fromEmployeeUpdateDtoToEmployeeDocument(null);
        assertNull(result);
    }
}

