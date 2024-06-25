package com.fleotadezuta.youthprogrammanager.model;

import graphql.GraphQLContext;
import lombok.Getter;

import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_ID;
import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_TYPE;

@Getter
public class UserDetails {
    private final String userId;
    private final String userType;

    public UserDetails(GraphQLContext context) {
        this.userId = context.get(APP_USER_ID).toString();
        this.userType = context.get(APP_USER_TYPE).toString();
    }
    

}
