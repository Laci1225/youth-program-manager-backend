package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.model.CurrentUserDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final Auth0Service auth0Service;

    public Mono<CurrentUserDto> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    var authentication = context.getAuthentication();
                    var auth0UserId = authentication.getName();
                    var appMetadata = auth0Service.getUserInfo(auth0UserId);
                    var userType = appMetadata.get("app_user_type");
                    var userId = appMetadata.get("app_user_id");
                    return new CurrentUserDto(Role.valueOf(userType), userId);
                });
    }
}
