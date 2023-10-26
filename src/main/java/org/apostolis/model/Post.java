package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apostolis.config.AppConfig;

import java.time.LocalDateTime;

/* Entity for post creation requests */

public class Post {
    private final int user;
    private final String text;
    private final LocalDateTime createdAt;


    @JsonCreator
    public Post(@JsonProperty("user") int user, @JsonProperty("text") String text) {
        this.user = user;
        this.text = text;
        createdAt = LocalDateTime.now(AppConfig.clock);
    }


    public int getUser() {
        return user;
    }


    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
