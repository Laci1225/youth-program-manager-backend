package com.fleotadezuta.youthprogrammanager.filter;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_ID;
import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_TYPE;

@Component
@Slf4j
@AllArgsConstructor
public class UserDetailsFilter implements WebFilter {

    private Auth0Service auth0Service;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {
        return ReactiveSecurityContextHolder.getContext().flatMap(context -> {

            var authentication = context.getAuthentication();
            log.error("Authentication: " + authentication);
            if (authentication != null) {
                var auth0UserId = authentication.getName();
                var appMetadata = auth0Service.getUserInfo(auth0UserId);
                var userType = appMetadata.getApp_user_type();
                var userId = appMetadata.getApp_user_id();

                return webFilterChain.filter(serverWebExchange.mutate()
                        .request(serverWebExchange.getRequest().mutate()
                                .header(APP_USER_TYPE, userType)
                                .header(APP_USER_ID, userId)
                                .build())
                        .build());
            }
            return webFilterChain.filter(serverWebExchange);
        });
    }
}
