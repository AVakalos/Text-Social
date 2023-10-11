package org.apostolis.model;

import java.util.Set;

public class User {

    private String username;
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
    public void setRole(Role role) {
        this.role = role;
    }

}
