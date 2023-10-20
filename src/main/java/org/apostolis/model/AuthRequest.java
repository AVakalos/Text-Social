package org.apostolis.model;

/* Request entity object for structured login requests */

public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest(){ }

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
}
