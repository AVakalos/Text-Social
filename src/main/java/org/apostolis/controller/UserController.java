package org.apostolis.controller;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import me.geso.tinyvalidator.ConstraintViolation;
import me.geso.tinyvalidator.Validator;
import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/* This REST controller class handles the signup, login and authentication requests */

public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public UserController(UserService userService){
        this.userService = userService;
    }

    public void signup(Context ctx) {
        User userFromContextBody = ctx.bodyAsClass(User.class);

        Validator validator = new Validator();
        List<ConstraintViolation> violations = validator.validate(userFromContextBody);
        if (!violations.isEmpty()) {
            String message = "Password or username must fulfill the bellow expectations:\n\n" +
                    violations.stream()
                    .map(v -> v.getName().concat(": ").concat(v.getMessage()))
                    .collect(Collectors.joining("\n"));
            throw new BadRequestResponse(message);
        }

        SignupResponse rsp = userService.signup(userFromContextBody);
        ctx.status(rsp.status());
        ctx.json(rsp);
    }
    public void login(Context ctx){
        AuthRequest loginFromContextBody = ctx.bodyAsClass(AuthRequest.class);
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
