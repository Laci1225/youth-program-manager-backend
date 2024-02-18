package com.fleotadezuta.youthprogrammanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class WebInterceptor implements WebGraphQlInterceptor {
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        var headers = request.getHeaders();
        var userTypeList = headers.get("X-app-user-type");
        var userIdList = headers.get("X-app-user-id");
        if (userTypeList != null && !userTypeList.isEmpty() && userIdList != null && !userIdList.isEmpty()) {
            var userType = userTypeList.get(0);
            var userId = userIdList.get(0);
            request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(Map.of("X-app-user-type", userType,
                            "X-app-user-id", userId)).build());
        }
        return chain.next(request);
    }
}
