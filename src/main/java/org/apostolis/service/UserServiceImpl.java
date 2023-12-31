package org.apostolis.service;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import jakarta.validation.ConstraintViolationException;
import org.apostolis.model.*;
import org.apostolis.repository.UserRepository;
import org.apostolis.security.PasswordEncoder;

import org.apostolis.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* This class implements the business logic of login, signup and user authentication. */

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository repository;
    private final TokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, TokenManager tokenManager, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean checkPassword(String username, String password) {
        try {
            User user = repository.getByUsername(username);
            String hashed_password = user.password();
            return passwordEncoder.checkPassword(password, hashed_password);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public SignupResponse signup(User UserToSave) {
        try {
            ValidationUtils.validateInput(UserToSave);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        try{
            if(repository.getByUsername(UserToSave.username()) != null){
                logger.warn("Username is already taken.");
                return new SignupResponse("Username is already taken! Try a different one.",406);
            }
        }catch(Exception e){
            throw new UnauthorizedResponse("Could not sign up");
        }
        repository.save(UserToSave, passwordEncoder);
        logger.info("User registered successfully");
        return new SignupResponse("User signed up.",201);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            ValidationUtils.validateInput(request);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        String inserted_username = request.username();
        String inserted_password = request.password();
        try{
            User user = repository.getByUsername(inserted_username);
            if(checkPassword(inserted_username,inserted_password)){
                String token = tokenManager.issueToken(inserted_username, Role.valueOf(user.role()));
                logger.info("User signed in successfully");
                return new AuthResponse(inserted_username,token,"User signed in",202);
            }
        }catch(Exception e){
            throw new BadRequestResponse("Could not sign in");
        }
        return new AuthResponse(inserted_username, "Not available","Could not sign in",401);
    }

    @Override
    public boolean authenticate(String token) {
        return tokenManager.validateToken(token);
    }
}
