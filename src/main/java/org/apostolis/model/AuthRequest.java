package org.apostolis.model;

/* Request entity object for structured login requests */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest {
    private final String username;
    private final String password;

    @JsonCreator
    public AuthRequest(@JsonProperty("username") String username, @JsonProperty("password")String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
