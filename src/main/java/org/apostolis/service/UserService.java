package org.apostolis.service;

import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;

public interface UserService {
    SignupResponse signup (User UserToSave);
    AuthResponse login (AuthRequest request);
    boolean authorize (String token, String userId);
}
