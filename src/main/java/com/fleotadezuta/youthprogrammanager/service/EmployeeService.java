package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.EmployeeUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final EmailService emailService;

    public Flux<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> deleteEmployee(UserDetails userDetails, String id) {
        /*Mono<String> myAuth0UserIdMono = ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getName());*/
        //todo doesnt work yet delete user by external id from auth0
        if (id.equals(userDetails.getUserId()))
            return Mono.error(new IllegalArgumentException("Cannot delete self"));

        return employeeRepository.findById(id)
                .flatMap(employee -> employeeRepository.deleteById(id).then(Mono.just(employee)))
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto)
                .flatMap(employeeDto -> {
                    /*
                    myAuth0UserIdMono.flatMap(authUser -> {
                    auth0Service.deleteUser(authUser);
                    */
                    return Mono.just(employeeDto);
                }).doOnSuccess(employeeDto ->
                        emailService.sendSimpleMessage(employeeDto.getEmail(),
                                "Account Deletion Confirmation",
                                "Dear " + employeeDto.getGivenName() + " " + employeeDto.getFamilyName() + ",\n\n" +
                                        "Your account has been successfully deleted from Youth Program Manager.\n\n" +
                                        "Best regards,\nYouth Program Manager Team")
                );
    }

    public Mono<EmployeeDto> updateEmployee(EmployeeUpdateDto employeeUpdateDto) {
        return employeeRepository.findById(employeeUpdateDto.getId())
                .flatMap(existingEmployeeDocument -> {
                    var employeeDocument = employeeMapper.fromEmployeeUpdateDtoToEmployeeDocument(employeeUpdateDto);
                    employeeDocument.setId(employeeUpdateDto.getId());
                    employeeDocument.setType(existingEmployeeDocument.getType());
                    return employeeRepository.save(employeeDocument);
                })
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto)
                .doOnSuccess(employeeDto ->
                        emailService.sendSimpleMessage(employeeDto.getEmail(),
                                "Profile Update Confirmation",
                                "Dear " + employeeDto.getGivenName() + " " + employeeDto.getFamilyName() + ",\n\n" +
                                        "Your profile has been successfully updated in the Youth Program Manager system. Please log in to review your updated information.\n\n" +
                                        "Best regards,\nYouth Program Manager Team")
                );
    }

    public Mono<EmployeeDto> addEmployee(EmployeeDto employeeDto) {
        return employeeRepository.save(employeeMapper.fromEmployeeDtoToEmployeeDocument(employeeDto))
                .doOnSuccess((employeeDocument) -> {
                    auth0Service.createUser(employeeDocument.getEmail(), employeeDocument.getId(), employeeDocument.getGivenName(), employeeDocument.getFamilyName(), employeeDocument.getType());
                    emailService.sendSimpleMessage(employeeDocument.getEmail(),
                            "Welcome to Youth Program Manager",
                            "Dear " + employeeDocument.getGivenName() + " " + employeeDocument.getFamilyName() + ",\n\n" +
                                    "Welcome to Youth Program Manager! Your account has been created successfully. Please log in to start using our services.\n\n" +
                                    "Best regards,\nYouth Program Manager Team");
                })
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }

    public Mono<EmployeeDto> getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::fromEmployeeDocumentToEmployeeDto);
    }
}
