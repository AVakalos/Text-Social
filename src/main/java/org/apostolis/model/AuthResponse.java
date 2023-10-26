package org.apostolis.model;

/* Response entity object for structured login responses  */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    private final String username;
    private final String token;
    private final String message;
    private final int status;


    @JsonCreator
    public AuthResponse(@JsonProperty("username") String username,
                        @JsonProperty("token") String token,
                        @JsonProperty("message") String message,
                        @JsonProperty("status") int status) {
        this.username = username;
        this.token = token;
        this.message = message;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
