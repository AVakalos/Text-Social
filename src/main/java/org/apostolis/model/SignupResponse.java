package org.apostolis.model;

/* Response entity for structured signup responses  */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignupResponse {
    private final String message;
    private final int status;

    @JsonCreator
    public SignupResponse(@JsonProperty("message") String message, @JsonProperty("status") int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

}
