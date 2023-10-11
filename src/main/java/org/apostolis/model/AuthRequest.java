package org.apostolis.model;

import me.geso.tinyvalidator.constraints.Email;
import me.geso.tinyvalidator.constraints.Size;

public class AuthRequest {
    @Email
    private String username;

    @Size(min = 8)
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

    public void setPassword(String password) {
        this.password = password;
    }
}
