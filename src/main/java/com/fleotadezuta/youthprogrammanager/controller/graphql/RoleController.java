package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.service.RoleService;
import graphql.GraphQLContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Controller
@AllArgsConstructor
@Slf4j
public class RoleController {
    private RoleService roleService;

    @QueryMapping("getAllRoles")
    public Flux<String> getAllRoles(GraphQLContext context) throws IOException {
        return roleService.getAllRoles(new UserDetails(context))
                .doOnComplete(() -> log.info("All roles fetched successfully"));
    }
}
