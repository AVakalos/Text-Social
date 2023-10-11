package org.apostolis.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordEncoder {

    public String encodePassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }
    public boolean checkPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
