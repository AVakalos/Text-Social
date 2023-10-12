package org.apostolis.controller;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.service.UserService;

public class UserController {

    private UserService userService;
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

    public void authorize (Context ctx){
        String token = ctx.header("Authorization").substring(7);
        if (token == null){
            throw new ForbiddenResponse();
        }
        boolean result = userService.authorize(token);
        if (!result){
            throw new ForbiddenResponse();
        }
    }
}
