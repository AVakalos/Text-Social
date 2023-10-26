package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apostolis.config.AppConfig;

import java.time.LocalDateTime;

/* Entity for comment creation requests */

public class Comment {
    private final int user;
    private final int post;
    private final String text;
    private final LocalDateTime createdAt;

    @JsonCreator
    public Comment(@JsonProperty("user") int user, @JsonProperty("post") int post, @JsonProperty("text") String text) {
        this.user = user;
        this.post = post;
        this.text = text;
        createdAt = LocalDateTime.now(AppConfig.clock);
    }

    public int getUser() {
        return user;
    }

    public int getPost() {
        return post;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

}
