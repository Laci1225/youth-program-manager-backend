package com.fleotadezuta.youthprogrammanager.unit.service;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.fixtures.service.EmployeeFixture;
import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.EmployeeUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.EmployeeDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.EmployeeRepository;
import com.fleotadezuta.youthprogrammanager.service.EmployeeService;
import graphql.GraphQLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.Principal;

import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_ID;
import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private Auth0Service auth0Service;

    @Mock
    private ReactiveSecurityContextHolder reactiveSecurityContextHolder;

    @Test
    void getAllEmployeesShouldReturnAllEmployees() {
        // Arrange
        when(employeeRepository.findAll())
                .thenReturn(Flux.fromIterable(EmployeeFixture.getEmployeeDocumentList()));
        when(employeeMapper.fromEmployeeDocumentToEmployeeDto(any(EmployeeDocument.class)))
                .thenAnswer(invocation -> {
                    EmployeeDocument document = invocation.getArgument(0);
                    int index = EmployeeFixture.getEmployeeDocumentList().indexOf(document);
                    return EmployeeFixture.getEmployeeDtoList().get(index);
                });

        // Act
        var employeeFlux = employeeService.getAllEmployees();

        // Assert
        StepVerifier.create(employeeFlux)
                .expectNextSequence(EmployeeFixture.getEmployeeDtoList())
                .verifyComplete();
        verify(employeeRepository, times(1)).findAll();
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void getEmployeeByIdShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById(anyString()))
                .thenReturn(Mono.just(EmployeeFixture.getEmployeeDocument()));
        when(employeeMapper.fromEmployeeDocumentToEmployeeDto(any(EmployeeDocument.class)))
                .thenReturn(EmployeeFixture.getEmployeeDto());

        // Act
        var employeeMono = employeeService.getEmployeeById("1234");

        // Assert
        assertThat(employeeMono.block()).usingRecursiveComparison().isEqualTo(EmployeeFixture.getEmployeeDto());
        verify(employeeRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void addEmployeeShouldSaveEmployee() {
        // Arrange
        when(employeeRepository.save(any(EmployeeDocument.class)))
                .thenReturn(Mono.just(EmployeeFixture.getEmployeeDocument()));
        when(employeeMapper.fromEmployeeDtoToEmployeeDocument(any(EmployeeDto.class)))
                .thenReturn(EmployeeFixture.getEmployeeDocument());
        when(employeeMapper.fromEmployeeDocumentToEmployeeDto(any(EmployeeDocument.class)))
                .thenReturn(EmployeeFixture.getEmployeeDto());

        // Act
        var employeeMono = employeeService.addEmployee(EmployeeFixture.getEmployeeDto());

        // Assert
        assertThat(employeeMono.block()).usingRecursiveComparison().isEqualTo(EmployeeFixture.getEmployeeDto());
        verify(employeeRepository, times(1)).save(any(EmployeeDocument.class));
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void updateEmployeeShouldUpdateEmployee() {
        // Arrange;
        EmployeeDocument updatedDocument = EmployeeFixture.getEmployeeDocument();
        updatedDocument.setGivenName(EmployeeFixture.getEmployeeUpdateDto().getGivenName());

        when(employeeRepository.findById(anyString())).thenReturn(Mono.just(EmployeeFixture.getEmployeeDocument()));
        when(employeeRepository.save(any(EmployeeDocument.class))).thenReturn(Mono.just(updatedDocument));
        when(employeeMapper.fromEmployeeUpdateDtoToEmployeeDocument(any(EmployeeUpdateDto.class)))
                .thenReturn(updatedDocument);
        when(employeeMapper.fromEmployeeDocumentToEmployeeDto(any(EmployeeDocument.class)))
                .thenReturn(EmployeeFixture.getEmployeeDto());

        // Act
        var employeeMono = employeeService.updateEmployee(EmployeeFixture.getEmployeeUpdateDto());

        // Assert
        assertThat(employeeMono.block()).usingRecursiveComparison().isEqualTo(EmployeeFixture.getEmployeeDto());
        verify(employeeRepository, times(1)).findById(anyString());
        verify(employeeRepository, times(1)).save(any(EmployeeDocument.class));
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void deleteEmployeeShouldDeleteEmployee() {
        // Arrange
        when(employeeRepository.findById(anyString()))
                .thenReturn(Mono.just(EmployeeFixture.getEmployeeDocument()));
        when(employeeRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());
        when(employeeMapper.fromEmployeeDocumentToEmployeeDto(any(EmployeeDocument.class)))
                .thenReturn(EmployeeFixture.getEmployeeDto());
        // Act
        var employeeMono = employeeService.deleteEmployee(
                new UserDetails(
                        GraphQLContext.newContext()
                                .of(APP_USER_ID, "parent123")
                                .of(APP_USER_TYPE, "TEACHER")
                                .build()), EmployeeFixture.getEmployeeDocument().getId());

        // Assert
        assertThat(employeeMono.block()).usingRecursiveComparison().isEqualTo(EmployeeFixture.getEmployeeDto());
        verify(employeeRepository, times(1)).findById(anyString());
        verify(employeeRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(employeeRepository);
        verifyNoMoreInteractions(auth0Service);
    }

    @Test
    void deleteEmployeeShouldFailWhenDeletingSelf() {
        // Act
        var employeeMono = employeeService.deleteEmployee(
                new UserDetails(
                        GraphQLContext.newContext()
                                .of(APP_USER_ID, "admin123")
                                .of(APP_USER_TYPE, "ADMINISTRATOR")
                                .build()), "admin123");

        // Assert
        StepVerifier.create(employeeMono)
                .expectErrorMessage("Cannot delete self")
                .verify();
    }
}
