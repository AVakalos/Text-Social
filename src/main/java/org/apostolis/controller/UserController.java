package org.apostolis.controller;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.repository.UserRepositoryImpl;
import org.apostolis.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {

    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public UserController(UserService userService){
        this.userService = userService;
    }

    public void signup(Context ctx) {
        User userFromContextBody = ctx.bodyAsClass(User.class);
        // Validate ....

        SignupResponse rsp = userService.signup(userFromContextBody);
        ctx.status(rsp.getStatus());
        ctx.result(rsp.getMessage());
    }
    public void login(Context ctx){
        AuthRequest loginFromContextBody = ctx.bodyAsClass(AuthRequest.class);
        AuthResponse rsp = userService.login(loginFromContextBody);
        ctx.status(rsp.getStatus());
        ctx.json(rsp);
    }

    public void authenticate (Context ctx){
        logger.info("Authenticate user");
        String token = ctx.header("Authorization");
        if (token != null){
            token = token.substring(7);
        }else{
            logger.error("Token is missing from incoming request");
            throw new ForbiddenResponse("Authentication token missing");
        }
        boolean result = userService.authenticate(token);
        if (!result){
            logger.error("Token is invalid");
            throw new ForbiddenResponse("Authentication token is invalid");
        }
        logger.info("User authenticated");
    }
}
