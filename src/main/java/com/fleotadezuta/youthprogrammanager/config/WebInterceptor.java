package com.fleotadezuta.youthprogrammanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_ID;
import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_TYPE;

@Slf4j
@Component
public class WebInterceptor implements WebGraphQlInterceptor {
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        var headers = request.getHeaders();
        var userTypeList = headers.get(APP_USER_TYPE);
        var userIdList = headers.get(APP_USER_ID);
        if (userTypeList != null && !userTypeList.isEmpty() && userIdList != null && !userIdList.isEmpty()) {
            var userType = userTypeList.get(0);
            var userId = userIdList.get(0);
            request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(Map.of(APP_USER_TYPE, userType,
                            APP_USER_ID, userId)).build());
        }
        return chain.next(request);
    }
}
