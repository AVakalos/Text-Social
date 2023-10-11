package org.apostolis.repository;

import org.apostolis.model.User;
import org.apostolis.security.PasswordEncoder;

public interface UserRepository {
    public void save(User UserToSave, PasswordEncoder passwordEncoder);
    public User getByUsername(String username) throws Exception;
}
