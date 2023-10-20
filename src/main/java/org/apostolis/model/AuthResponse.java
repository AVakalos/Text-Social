package org.apostolis.model;

/* Response entity object for structured login responses  */

public class AuthResponse {
    private String username;
    private String token;
    private String message;
    private int status;

    public AuthResponse(){ }

    public AuthResponse(String username, String token, String message,int status) {
        this.username = username;
        this.token = token;
        this.message = message;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
