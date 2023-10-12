package org.apostolis.model;

public class Comment {
    private int id;

    private int user;

    private int post;

    private String text;

    public Comment(int user, int post, String text) {
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

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }
}
