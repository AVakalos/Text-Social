package org.apostolis.controller;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.service.OperationsService;

import java.util.Objects;

public class OperationsController {

    private final OperationsService operationsService;

    public OperationsController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    public void create_post(Context ctx){
        Post postToSave = ctx.bodyAsClass(Post.class);
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        operationsService.create_post(postToSave, token);
        ctx.result("User: "+postToSave.getUser()+" did a new post");
    }

    public void create_comment(Context ctx){
        Comment commentToSave = ctx.bodyAsClass(Comment.class);
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        operationsService.create_comment(commentToSave, token);
        ctx.result("User: "+commentToSave.getUser()+" commented on a post");
    }

    public void follow(Context ctx){
        try{
            int user = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("user")));
            int follows = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("follows")));
            operationsService.follow(user,follows);
            ctx.result("User: "+user+" followed user: "+follows);
        }catch(NullPointerException e){
            ctx.status(500);
            ctx.result("Query params are null");
        }
    }

    public void unfollow(Context ctx){
        try{
            int user = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("user")));
            int unfollows = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("unfollows")));
            operationsService.unfollow(user,unfollows);
            ctx.result("User: "+user+" unfollowed user: "+unfollows);
        }catch(NullPointerException e){
            ctx.status(500);
            ctx.result("Query params are null");
        }
    }

    public void create_url_for_post(Context ctx) {
        int user = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("id")));
        int post = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("post")));
        ctx.result(operationsService.create_url_for_post_and_comments(user, post));
    }

    public void decode_url(Context ctx) {
        try{
            ctx.result(operationsService.decode_url(ctx.url()));
        }catch (Exception e){
            throw new NotFoundResponse();
        }
    }
}
