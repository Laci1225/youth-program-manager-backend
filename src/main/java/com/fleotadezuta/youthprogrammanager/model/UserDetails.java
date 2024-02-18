package com.fleotadezuta.youthprogrammanager.model;

import graphql.GraphQLContext;
import lombok.Getter;

@Getter
public class UserDetails {
    private final String userId;
    private final String userType;

    public UserDetails(GraphQLContext context) {
        this.userId = context.get("X-app-user-id").toString();
        this.userType = context.get("X-app-user-type").toString();
    }

}
