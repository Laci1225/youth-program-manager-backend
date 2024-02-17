package com.fleotadezuta.youthprogrammanager.controller.graphql;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.sun.tools.javac.Main;
import jakarta.validation.Valid;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Controller
@AllArgsConstructor
@Slf4j
public class ParentController {

    private final ChildParentFacade childParentFacade;

    @QueryMapping("getAllParents")
    public Flux<ParentUpdateDto> getAllParents() {
        var accesstoken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImVGRHdYN0FVdFJYWEczZ2UxSGY0dCJ9.eyJpc3MiOiJodHRwczovL2Rldi13bnVmNWVuc2s0ZG5xdWNuLmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJWWDhyTmoxSTBvbmQwRnhIcUJna25xU0FxbUQ4UDdoTkBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtd251ZjVlbnNrNGRucXVjbi5ldS5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTcwODE4ODAxNCwiZXhwIjoxNzA4Mjc0NDE0LCJhenAiOiJWWDhyTmoxSTBvbmQwRnhIcUJna25xU0FxbUQ4UDdoTiIsInNjb3BlIjoicmVhZDpjbGllbnRfZ3JhbnRzIGNyZWF0ZTpjbGllbnRfZ3JhbnRzIGRlbGV0ZTpjbGllbnRfZ3JhbnRzIHVwZGF0ZTpjbGllbnRfZ3JhbnRzIHJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIGRlbGV0ZTp1c2VycyBjcmVhdGU6dXNlcnMgcmVhZDp1c2Vyc19hcHBfbWV0YWRhdGEgdXBkYXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBkZWxldGU6dXNlcnNfYXBwX21ldGFkYXRhIGNyZWF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgcmVhZDp1c2VyX2N1c3RvbV9ibG9ja3MgY3JlYXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBkZWxldGU6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX3RpY2tldHMgcmVhZDpjbGllbnRzIHVwZGF0ZTpjbGllbnRzIGRlbGV0ZTpjbGllbnRzIGNyZWF0ZTpjbGllbnRzIHJlYWQ6Y2xpZW50X2tleXMgdXBkYXRlOmNsaWVudF9rZXlzIGRlbGV0ZTpjbGllbnRfa2V5cyBjcmVhdGU6Y2xpZW50X2tleXMgcmVhZDpjb25uZWN0aW9ucyB1cGRhdGU6Y29ubmVjdGlvbnMgZGVsZXRlOmNvbm5lY3Rpb25zIGNyZWF0ZTpjb25uZWN0aW9ucyByZWFkOnJlc291cmNlX3NlcnZlcnMgdXBkYXRlOnJlc291cmNlX3NlcnZlcnMgZGVsZXRlOnJlc291cmNlX3NlcnZlcnMgY3JlYXRlOnJlc291cmNlX3NlcnZlcnMgcmVhZDpkZXZpY2VfY3JlZGVudGlhbHMgdXBkYXRlOmRldmljZV9jcmVkZW50aWFscyBkZWxldGU6ZGV2aWNlX2NyZWRlbnRpYWxzIGNyZWF0ZTpkZXZpY2VfY3JlZGVudGlhbHMgcmVhZDpydWxlcyB1cGRhdGU6cnVsZXMgZGVsZXRlOnJ1bGVzIGNyZWF0ZTpydWxlcyByZWFkOnJ1bGVzX2NvbmZpZ3MgdXBkYXRlOnJ1bGVzX2NvbmZpZ3MgZGVsZXRlOnJ1bGVzX2NvbmZpZ3MgcmVhZDpob29rcyB1cGRhdGU6aG9va3MgZGVsZXRlOmhvb2tzIGNyZWF0ZTpob29rcyByZWFkOmFjdGlvbnMgdXBkYXRlOmFjdGlvbnMgZGVsZXRlOmFjdGlvbnMgY3JlYXRlOmFjdGlvbnMgcmVhZDplbWFpbF9wcm92aWRlciB1cGRhdGU6ZW1haWxfcHJvdmlkZXIgZGVsZXRlOmVtYWlsX3Byb3ZpZGVyIGNyZWF0ZTplbWFpbF9wcm92aWRlciBibGFja2xpc3Q6dG9rZW5zIHJlYWQ6c3RhdHMgcmVhZDppbnNpZ2h0cyByZWFkOnRlbmFudF9zZXR0aW5ncyB1cGRhdGU6dGVuYW50X3NldHRpbmdzIHJlYWQ6bG9ncyByZWFkOmxvZ3NfdXNlcnMgcmVhZDpzaGllbGRzIGNyZWF0ZTpzaGllbGRzIHVwZGF0ZTpzaGllbGRzIGRlbGV0ZTpzaGllbGRzIHJlYWQ6YW5vbWFseV9ibG9ja3MgZGVsZXRlOmFub21hbHlfYmxvY2tzIHVwZGF0ZTp0cmlnZ2VycyByZWFkOnRyaWdnZXJzIHJlYWQ6Z3JhbnRzIGRlbGV0ZTpncmFudHMgcmVhZDpndWFyZGlhbl9mYWN0b3JzIHVwZGF0ZTpndWFyZGlhbl9mYWN0b3JzIHJlYWQ6Z3VhcmRpYW5fZW5yb2xsbWVudHMgZGVsZXRlOmd1YXJkaWFuX2Vucm9sbG1lbnRzIGNyZWF0ZTpndWFyZGlhbl9lbnJvbGxtZW50X3RpY2tldHMgcmVhZDp1c2VyX2lkcF90b2tlbnMgY3JlYXRlOnBhc3N3b3Jkc19jaGVja2luZ19qb2IgZGVsZXRlOnBhc3N3b3Jkc19jaGVja2luZ19qb2IgcmVhZDpjdXN0b21fZG9tYWlucyBkZWxldGU6Y3VzdG9tX2RvbWFpbnMgY3JlYXRlOmN1c3RvbV9kb21haW5zIHVwZGF0ZTpjdXN0b21fZG9tYWlucyByZWFkOmVtYWlsX3RlbXBsYXRlcyBjcmVhdGU6ZW1haWxfdGVtcGxhdGVzIHVwZGF0ZTplbWFpbF90ZW1wbGF0ZXMgcmVhZDptZmFfcG9saWNpZXMgdXBkYXRlOm1mYV9wb2xpY2llcyByZWFkOnJvbGVzIGNyZWF0ZTpyb2xlcyBkZWxldGU6cm9sZXMgdXBkYXRlOnJvbGVzIHJlYWQ6cHJvbXB0cyB1cGRhdGU6cHJvbXB0cyByZWFkOmJyYW5kaW5nIHVwZGF0ZTpicmFuZGluZyBkZWxldGU6YnJhbmRpbmcgcmVhZDpsb2dfc3RyZWFtcyBjcmVhdGU6bG9nX3N0cmVhbXMgZGVsZXRlOmxvZ19zdHJlYW1zIHVwZGF0ZTpsb2dfc3RyZWFtcyBjcmVhdGU6c2lnbmluZ19rZXlzIHJlYWQ6c2lnbmluZ19rZXlzIHVwZGF0ZTpzaWduaW5nX2tleXMgcmVhZDpsaW1pdHMgdXBkYXRlOmxpbWl0cyBjcmVhdGU6cm9sZV9tZW1iZXJzIHJlYWQ6cm9sZV9tZW1iZXJzIGRlbGV0ZTpyb2xlX21lbWJlcnMgcmVhZDplbnRpdGxlbWVudHMgcmVhZDphdHRhY2tfcHJvdGVjdGlvbiB1cGRhdGU6YXR0YWNrX3Byb3RlY3Rpb24gcmVhZDpvcmdhbml6YXRpb25zX3N1bW1hcnkgY3JlYXRlOmF1dGhlbnRpY2F0aW9uX21ldGhvZHMgcmVhZDphdXRoZW50aWNhdGlvbl9tZXRob2RzIHVwZGF0ZTphdXRoZW50aWNhdGlvbl9tZXRob2RzIGRlbGV0ZTphdXRoZW50aWNhdGlvbl9tZXRob2RzIHJlYWQ6b3JnYW5pemF0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9ucyBkZWxldGU6b3JnYW5pemF0aW9ucyBjcmVhdGU6b3JnYW5pemF0aW9uX21lbWJlcnMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVycyBkZWxldGU6b3JnYW5pemF0aW9uX21lbWJlcnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyByZWFkOm9yZ2FuaXphdGlvbl9jb25uZWN0aW9ucyB1cGRhdGU6b3JnYW5pemF0aW9uX2Nvbm5lY3Rpb25zIGRlbGV0ZTpvcmdhbml6YXRpb25fY29ubmVjdGlvbnMgY3JlYXRlOm9yZ2FuaXphdGlvbl9tZW1iZXJfcm9sZXMgcmVhZDpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGRlbGV0ZTpvcmdhbml6YXRpb25fbWVtYmVyX3JvbGVzIGNyZWF0ZTpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgcmVhZDpvcmdhbml6YXRpb25faW52aXRhdGlvbnMgZGVsZXRlOm9yZ2FuaXphdGlvbl9pbnZpdGF0aW9ucyBkZWxldGU6cGhvbmVfcHJvdmlkZXJzIGNyZWF0ZTpwaG9uZV9wcm92aWRlcnMgcmVhZDpwaG9uZV9wcm92aWRlcnMgdXBkYXRlOnBob25lX3Byb3ZpZGVycyBkZWxldGU6cGhvbmVfdGVtcGxhdGVzIGNyZWF0ZTpwaG9uZV90ZW1wbGF0ZXMgcmVhZDpwaG9uZV90ZW1wbGF0ZXMgdXBkYXRlOnBob25lX3RlbXBsYXRlcyBjcmVhdGU6ZW5jcnlwdGlvbl9rZXlzIHJlYWQ6ZW5jcnlwdGlvbl9rZXlzIHVwZGF0ZTplbmNyeXB0aW9uX2tleXMgZGVsZXRlOmVuY3J5cHRpb25fa2V5cyByZWFkOnNlc3Npb25zIGRlbGV0ZTpzZXNzaW9ucyByZWFkOnJlZnJlc2hfdG9rZW5zIGRlbGV0ZTpyZWZyZXNoX3Rva2VucyByZWFkOmNsaWVudF9jcmVkZW50aWFscyBjcmVhdGU6Y2xpZW50X2NyZWRlbnRpYWxzIHVwZGF0ZTpjbGllbnRfY3JlZGVudGlhbHMgZGVsZXRlOmNsaWVudF9jcmVkZW50aWFscyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.hhnwmuht3ObMm4xy69dzitObPLSlmaC9v436hRbAffpwjZYU30XS9K0hrit8nZK1WzfQOieuim6bHhlD1YCyyiuFyl3kzs5U9LbCowSG0WeAL0njyVgAjwli9e7VMnwxZwYC47x3T63fMmJnqrqYIUvnHTQ-4T8_XmEBB-BKKTESfQTf5WExm_ms7ZxMbgGeJL8MQfjNT4mycIGJNwiGPPXidFSIyagcP6kgjawyGDMhUQ1q5V9AGEZs6Vh-kVoHrvoGkSGSzoIJTIPaPyWk3YnjzwvkT_ygL2yLPkyomql2KB4A5tjAGt3ECkEFoKMPgEBmQ_HCR70CXBjUzqTZ0w";
        HttpResponse<String> response = Unirest.post("https://dev-wnuf5ensk4dnqucn.eu.auth0.com/api/v2/users/auth0%7C65b6462677cdcbbf47524ce6/roles")
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + accesstoken)
                .header("cache-control", "no-cache")
                .body("{\"roles\":[\"rol_ZVbZjai55NpwOwhV\",\"rol_Mjt9yu2PlPadWRn5\"]}")
                .asString();
        //everything iwth id nothing with name
        System.out.println("Response code: " + response.getStatus());
        System.out.println("Response body: " + response.getBody());
        log.error(response.getHeaders().toString());
        log.error(response.getCookies().toString());
        log.error(response.getBody());
        return childParentFacade.getAllParents()
                .doOnComplete(() -> log.info("All parents fetched successfully"));
    }

    @QueryMapping("getParentById")
    public Mono<ParentWithChildrenDto> getParentById(@Argument String id) {
        return childParentFacade.getParentById(id)
                .doOnSuccess(parentDto -> log.info("Retrieved Parent by ID: " + id));
    }

    @MutationMapping("addParent")
    public Mono<ParentDto> addParent(@Valid @RequestBody @Argument ParentCreateDto parent) {
        Properties properties = new Properties();
        InputStream input = Main.class.getClassLoader().getResourceAsStream("application-local.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var a = childParentFacade.addParent(parent)
                .doOnSuccess(parentDto -> log.info("Added Parent with data: " + parentDto));
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            //String accessToken = jwtAuthenticationToken.getToken().getTokenValue();

            /*HttpResponse<String> response = Unirest.post("https://dev-wnuf5ensk4dnqucn.eu.auth0.com/api/v2/users")
                    .header("authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImVGRHdYN0FVdFJYWEczZ2UxSGY0dCJ9.eyJpc3MiOiJodHRwczovL2Rldi13bnVmNWVuc2s0ZG5xdWNuLmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw2NWI2NDYyNjc3Y2RjYmJmNDc1MjRjZTYiLCJhdWQiOlsiaHR0cHM6Ly9kZXYtd251ZjVlbnNrNGRucXVjbi5ldS5hdXRoMC5jb20vYXBpL3YyLyIsImh0dHBzOi8vZGV2LXdudWY1ZW5zazRkbnF1Y24uZXUuYXV0aDAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTcwODE4NTEwNywiZXhwIjoxNzA4MjcxNTA3LCJhenAiOiJXSFdyNzE4NXBWd2ZreldHa1I0RUwzQ0YweHA5YllCeCIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgdXBkYXRlOmN1cnJlbnRfdXNlcl9tZXRhZGF0YSJ9.j1DcDTJXDyltM8KefiIGhqF3go1t1IM1gQ8K9WgcHJS3AqVOghWn1hdc8jEehUaKlmYU7EXevdxu965Jaqk6GhdRaDYY77IQgzHRj44Yko0i3Qa2Ca6ejey0te6yzAOInVmjtB8YDuy8tiCxFraKGEKij-4rrGeRbamEBlvXK7IXO5k4j7H2JyeYhJ9qEXur1lcYqtH6G-dOWtY-vFEC6JhW_heHM-gFR8zLhjwbJVXhQWOIe8BBlWoWp2BmZ4_Hma2FWOgwIhQ8tBPmvuRncYSEz5Vd5Dh_qz2GxbFzDBwN_vL4UxhdEai2jo3ydR_unSsO6E78sSMAMQT4EpC8dA")
                    .header("content-type", "application/json")
                    .body("{\"app_metadata\": {\"appId\": \"1231224124\"}}")
                    .asString();*/

        }
        return a;
    }

    @MutationMapping("updateParent")
    public Mono<ParentDto> updateParent(@Valid @RequestBody @Argument ParentUpdateDto parent) {
        return childParentFacade.updateParent(parent)
                .doOnSuccess(parentDto -> log.info("Updated Parent: " + parentDto));
    }

    @MutationMapping("deleteParent")
    public Mono<ParentDto> deleteParent(@Argument String id) {
        return childParentFacade.deleteParent(id)
                .doOnSuccess(deletedParent -> log.info("Deleted Parent with ID: " + deletedParent.getId()));
    }

    @QueryMapping("getPotentialChildren")
    public Flux<ChildDto> getPotentialParents(@Argument String name) {
        return childParentFacade.getPotentialChildren(name)
                .doOnNext(parent -> log.info("Child with name " + parent.getGivenName() + " " + parent.getFamilyName() + " fetched successfully"));
    }
}
