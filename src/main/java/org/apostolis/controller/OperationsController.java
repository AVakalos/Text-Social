package org.apostolis.controller;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.apostolis.model.*;
import org.apostolis.service.OperationsService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/* This REST controller class handles requests regarding operations that need data storing
*  such as create post and comment as well as the link creation and decoding  */

public class OperationsController {

    private final OperationsService operationsService;

    public OperationsController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    public void createPost(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        Post postToSave = ctx.bodyAsClass(Post.class);
        operationsService.createPost(postToSave, token);
        ctx.result("User: "+operationsService.getUsername(postToSave.user())+" did a new post");
    }

    public void createComment(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        Comment commentToSave = ctx.bodyAsClass(Comment.class);
        operationsService.createComment(commentToSave, token);
        ctx.result("User: "+operationsService.getUsername(commentToSave.user())+" commented on a post");
    }

    public void follow(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = ctx.queryParamAsClass("user", Integer.class).get();
        int follows = ctx.queryParamAsClass("follows", Integer.class).get();
        FollowRequest followRequest = new FollowRequest(user, follows);

        operationsService.follow(followRequest, token);
        ctx.result("User: "+operationsService.getUsername(user)+
                " followed user: "+operationsService.getUsername(follows));
    }

    public void unfollow(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = ctx.queryParamAsClass("user", Integer.class).get();
        int unfollows = ctx.queryParamAsClass("unfollows", Integer.class).get();

        operationsService.unfollow(new UnfollowRequest(user,unfollows), token);
        ctx.result("User: "+operationsService.getUsername(user)+
                " unfollowed user: "+operationsService.getUsername(unfollows));
    }

    public void createUrlForPostAndComments(Context ctx) {
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);

        int user = ctx.pathParamAsClass("id", Integer.class).get();
        int post = ctx.pathParamAsClass("post", Integer.class).get();

        String generated_url = operationsService.createUrlForPostAndComments(new CreateLinkRequest(user, post), token);
        ctx.result(generated_url);
    }

    public void decodeLink(Context ctx) {
        try{
            HashMap<String, ArrayList<String>> post_and_comments =
                    operationsService.decodeUrl(new DecodeRequest(ctx.pathParam("url")));
            JSONObject jsonResults = new JSONObject(post_and_comments);
            ctx.result(jsonResults.toString());
        }catch (Exception e){
            throw new NotFoundResponse();
        }
    }
}
