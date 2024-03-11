package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;

    public Flux<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> deleteEmployee(UserDetails userDetails, String id) {
        return employeeRepository.findById(id)
                .flatMap(employee -> employeeRepository.deleteById(id)
                        .then(Mono.just(employee)))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> updateEmployee(UserDetails userDetails, EmployeeDto employeeDto) {
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> addEmployee(EmployeeDto employeeDto) {
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> getEmployeeById(UserDetails userDetails, String id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }
}
