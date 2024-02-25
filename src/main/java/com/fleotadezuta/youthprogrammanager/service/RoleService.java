package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import okhttp3.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Service
public class RoleService {
    public Flux<String> getAllRoles(UserDetails userDetails) throws IOException {
        return Flux.just(userDetails.getUserType());
    }
}
