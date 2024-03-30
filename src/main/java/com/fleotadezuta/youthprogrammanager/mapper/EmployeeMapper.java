package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.EmployeeUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDto fromEmployeeDocumentToEmployeeDto(EmployeeDocument employee);

    EmployeeDocument fromEmployeeDtoToEmployeeDocument(EmployeeDto employeeDto);

    EmployeeDocument fromEmployeeUpdateDtoToEmployeeDocument(EmployeeUpdateDto employeeDto);

}
