package org.apostolis.security;

import org.apostolis.model.Role;

public interface TokenManager {
    String issueToken (String username, Role role);
    boolean validateToken (String token);
    Role extractRole(String token);
}
