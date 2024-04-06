package com.fleotadezuta.youthprogrammanager.controller.rest;


import com.fleotadezuta.youthprogrammanager.facade.UserFacade;
import com.fleotadezuta.youthprogrammanager.model.CurrentUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserFacade userFacade;

    @GetMapping("/me")
    public Mono<CurrentUserDto> getCurrentUser() {
        return userFacade.getCurrentUser();
    }
}
