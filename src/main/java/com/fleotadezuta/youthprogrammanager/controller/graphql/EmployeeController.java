package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.EmployeeDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.service.EmployeeService;
import graphql.GraphQLContext;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('list:employees')")
    @QueryMapping("getAllEmployees")
    public Flux<EmployeeDto> getAllEmployees(GraphQLContext context) {
        return employeeService.getAllEmployees(new UserDetails(context))
                .doOnComplete(() -> log.info("All employees fetched successfully"));
    }

    @PreAuthorize("hasAuthority('read:employees')")
    @QueryMapping("getEmployeeById")
    public Mono<EmployeeDto> getEmployeeById(GraphQLContext context, @Argument String id) {
        return employeeService.getEmployeeById(new UserDetails(context), id)
                .doOnSuccess(employeeDto -> log.info("Retrieved Employee: " + employeeDto));
    }

    @PreAuthorize("hasAuthority('create:employees')")
    @MutationMapping("addEmployee")
    public Mono<EmployeeDto> addEmployee(GraphQLContext context, @Valid @RequestBody @Argument EmployeeDto employee) {
        return employeeService.addEmployee(new UserDetails(context), employee)
                .doOnSuccess(employeeDto -> log.info("Added Employee with data: " + employeeDto));
    }

    @PreAuthorize("hasAuthority('update:employees')")
    @MutationMapping("updateEmployee")
    public Mono<EmployeeDto> updateEmployee(GraphQLContext context, @Valid @RequestBody @Argument EmployeeDto employee) {
        return employeeService.updateEmployee(new UserDetails(context), employee)
                .doOnSuccess(employeeDto -> log.info("Updated Employee: " + employeeDto));
    }

    @PreAuthorize("hasAuthority('delete:employees')")
    @MutationMapping("deleteEmployee")
    public Mono<EmployeeDto> deleteEmployee(GraphQLContext context, @Argument String id) {
        return employeeService.deleteEmployee(new UserDetails(context), id)
                .doOnSuccess(deletedEmployee -> log.info("Deleted Employee with ID: " + deletedEmployee.getId()));
    }
}
