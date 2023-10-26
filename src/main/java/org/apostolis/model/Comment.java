package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apostolis.config.AppConfig;

import java.time.LocalDateTime;

/* Entity for comment creation requests */

public record Comment(
        @Positive
        int user,
        @Positive
        int post,
        @NotNull
        @NotBlank
        String text,
        LocalDateTime createdAt
){
    @JsonCreator
    public Comment(@JsonProperty("user") int user, @JsonProperty("post") int post, @JsonProperty("text") String text) {
        this(user,post,text,LocalDateTime.now(AppConfig.clock));
    }
}
