package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Entity for comment creation requests */

@JsonIgnoreProperties({ "id" })
public class Comment {
    private int user;
    private int post;
    private final String text;

    @JsonCreator
    public Comment(@JsonProperty("user") int user, @JsonProperty("post") int post, @JsonProperty("text") String text) {
        this.user = user;
        this.post = post;
        this.text = text;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getText() {
        return text;
    }

}
