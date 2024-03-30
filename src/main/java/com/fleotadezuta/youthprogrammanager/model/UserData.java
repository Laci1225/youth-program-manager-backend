package com.fleotadezuta.youthprogrammanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserData(String email,
                       @JsonProperty("app_metadata") AppMetadata appMetadata,
                       @JsonProperty("family_name") String familyName,
                       @JsonProperty("given_name") String givenName,
                       String connection) {
}
