package org.apostolis.controller;

import io.javalin.http.Context;
import org.apostolis.model.Post;
import org.apostolis.service.OperationsService;

public class OperationsController {

    private OperationsService operationsService;

    public OperationsController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    public void create_post(Context ctx){
        ctx.bodyAsClass(Post.class);
        String token = ctx.header("Authorization").substring(7);
    }

    public void create_comment(Context ctx){

    }

    public void follow(Context ctx){

    }

    public void unfollow(Context ctx){

    }
}
