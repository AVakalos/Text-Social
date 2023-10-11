package org.apostolis.security;

import org.apostolis.model.Role;

public interface TokenManager {
    String issueToken (String username, Role role);
    boolean authorize (String token, String userId);
}
