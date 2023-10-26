package org.apostolis.controller;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import jakarta.validation.ConstraintViolationException;
import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.service.UserService;
import org.apostolis.service.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* This REST controller class handles the signup, login and authentication requests */

public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public UserController(UserService userService){
        this.userService = userService;
    }

    public void signup(Context ctx) {
        User userFromContextBody = ctx.bodyAsClass(User.class);

        try {
            ValidationUtils.validateInput(userFromContextBody);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        SignupResponse rsp = userService.signup(userFromContextBody);
        ctx.status(rsp.status());
        ctx.json(rsp);
    }
    public void login(Context ctx){
        AuthRequest loginFromContextBody = ctx.bodyAsClass(AuthRequest.class);
        try {
            ValidationUtils.validateInput(loginFromContextBody);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        AuthResponse rsp = userService.login(loginFromContextBody);
        ctx.status(rsp.status());
        ctx.json(rsp);
    }

    // Check the token provided with the request and authenticate the user to allow interaction with the system.
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
