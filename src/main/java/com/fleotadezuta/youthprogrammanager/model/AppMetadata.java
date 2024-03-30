package com.fleotadezuta.youthprogrammanager.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public record AppMetadata(@JsonProperty("app_user_id") String appUserId,
                          @JsonProperty("app_user_type") String appUserType) {
    public AppMetadata() {
        this(null, null);
    }
}
