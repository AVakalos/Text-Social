package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Entity for post creation requests */

@JsonIgnoreProperties({ "id" })
public class Post {
    private int user;
    private final String text;

    @JsonCreator
    public Post(@JsonProperty("user") int user, @JsonProperty("text") String text) {
        this.user = user;
        this.text = text;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }
}
