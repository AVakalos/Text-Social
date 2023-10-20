package org.apostolis.model;

import me.geso.tinyvalidator.constraints.Email;
import me.geso.tinyvalidator.constraints.Size;

/* User entity for signup requests */

public class User {
    @Email
    private String username;
    @Size(min = 8)
    private String password;
    private Role role;

    public User(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = Role.valueOf(role);
    }
    public User() {}

    public String getUsername() {
        return username;
    }
    public String getPassword(){
        return password;
    }
    public Role getRole() {
        return role;
    }
}
