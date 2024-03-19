package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ParentEmployeeFacade;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MeController {
    private final ParentEmployeeFacade parentEmployeeFacade;

    /*@QueryMapping("me")
    public Mono<String> me(GraphQLContext context) {
        return parentEmployeeFacade.getMe(new UserDetails(context))
                .doOnSuccess(me -> log.info("Retrieved Entity: " + me));
    }*/
}
