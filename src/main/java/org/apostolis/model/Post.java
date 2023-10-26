package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apostolis.config.AppConfig;

import java.time.LocalDateTime;

/* Entity for post creation requests */

public record Post (@Positive int user, @NotNull @NotBlank String text, LocalDateTime createdAt){
    @JsonCreator
    public Post(@JsonProperty("user") int user,@JsonProperty("text") String text){
        this(user,text,LocalDateTime.now(AppConfig.clock));
    }
}