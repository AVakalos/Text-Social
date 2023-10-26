package org.apostolis.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.geso.tinyvalidator.constraints.Email;
import me.geso.tinyvalidator.constraints.Size;

/* User entity for signup requests */

public class User {
    @Email
    private final String username;
    @Size(min = 8)
    private final String password;
    private final Role role;

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("role") String role){
        this.username = username;
        this.password = password;
        this.role = Role.valueOf(role);
    }

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
