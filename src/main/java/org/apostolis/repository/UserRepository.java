package org.apostolis.repository;

import org.apostolis.model.User;
import org.apostolis.security.PasswordEncoder;

public interface UserRepository {
    void save(User UserToSave, PasswordEncoder passwordEncoder);
    User getByUsername(String username) throws Exception;
}
