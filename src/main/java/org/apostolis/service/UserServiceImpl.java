package org.apostolis.service;

import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.repository.UserRepository;
import org.apostolis.security.PasswordEncoder;

import org.apostolis.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository repository;
    private TokenManager tokenManager;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, TokenManager tokenManager, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean checkPassword(String username, String password) {
        try {
            User user = repository.getByUsername(username);
            String hashed_password = user.getPassword();
            return passwordEncoder.checkPassword(password, hashed_password);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public SignupResponse signup(User UserToSave) {
        SignupResponse response = new SignupResponse("Username is already taken! Try a different one.",406);
        try{
            logger.info("Checking if username is already taken...");

            if(repository.getByUsername(UserToSave.getUsername()) != null){
                logger.info("Username is already taken.");
                return response;
            }else{
                logger.info("The username is available for signup.");
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        repository.save(UserToSave, passwordEncoder);
        response.setMessage("User signed up.");
        response.setStatus(201);
        return response;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String inserted_username = request.getUsername();
        String inserted_password = request.getPassword();
        AuthResponse response = new AuthResponse(
                inserted_username,"Not available","Invalid username or password",401);
        try{
            User user = repository.getByUsername(inserted_username);
            if(checkPassword(inserted_username,inserted_password)){
                String token = tokenManager.issueToken(inserted_username,user.getRole());
                response.setToken(token);
                response.setMessage("User signed in.");
                response.setStatus(202);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return response;
    }

    @Override
    public boolean authenticate(String token) {
        return tokenManager.validateToken(token);
    }
}
