package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.EmployeeMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import com.fleotadezuta.youthprogrammanager.service.EmployeeService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParentEmployeeFacade {

    /*private final ChildParentFacade childParentFacade
    private final EmployeeService employeeService;
    private final ParentMapper parentMapper;
    private final EmployeeMapper employeeMapper;

    public Mono<ParentEmployeeDto> getMe(UserDetails userDetails) {
        if (userDetails.getUserType().equals(Role.PARENT.name())) {
            return childParentFacade.getParentById(userDetails,userDetails.getUserId());
        } else {
            return employeeService.getEmployeeById(userDetails,userDetails.getUserId());
        }
    }*/
}
