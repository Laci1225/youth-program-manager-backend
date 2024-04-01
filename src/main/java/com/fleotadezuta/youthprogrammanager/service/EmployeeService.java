package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.EmployeeUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final Auth0Service auth0Service;

    public Flux<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> deleteEmployee(UserDetails userDetails, String id) {
        Mono<String> auth0UserIdMono = ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getName());
        if (id.equals(userDetails.getUserId()))
            return Mono.error(new RuntimeException("Cannot delete self"));
        return employeeRepository.findById(id)
                .flatMap(employee -> employeeRepository.deleteById(id).then(Mono.just(employee)))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto)
                .flatMap(employeeDto -> auth0UserIdMono.flatMap(authUser -> {
                    auth0Service.deleteUser(authUser);
                    return Mono.just(employeeDto);
                }));
    }

    public Mono<EmployeeDto> updateEmployee(EmployeeUpdateDto employeeDto) {
        return employeeRepository.findById(employeeDto.getId())
                .flatMap(existingEmployeeDocument -> {
                    var employeeDocument = employeeMapper.fromEmployeeUpdateDtoToEmployeeDocument(employeeDto);
                    employeeDocument.setId(employeeDto.getId());
                    employeeDocument.setType(existingEmployeeDocument.getType());
                    return employeeRepository.save(employeeDocument);
                })
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> addEmployee(EmployeeDto employeeDto) {
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .doOnSuccess((employeeDocument) -> auth0Service.createUser(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), employeeDocument.getType()))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }
}
