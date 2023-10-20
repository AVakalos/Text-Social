package org.apostolis.controller;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.service.OperationsService;
import org.apostolis.service.RequestValidationService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/* This REST controller class handles requests regarding operations that need data storing
*  such as create post and comment as well as the link creation and decoding  */

public class OperationsController {

    private final OperationsService operationsService;
    private final RequestValidationService requestValidationService;

    public OperationsController(OperationsService operationsService, RequestValidationService requestValidationService) {
        this.operationsService = operationsService;
        this.requestValidationService = requestValidationService;
    }

    public void createPost(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);

        Post postToSave = ctx.bodyValidator(Post.class).check(
                post -> post.getUser() == requestValidationService.extractUserId(token),
                "You are not allowed to make a post as another user").get();

        operationsService.createPost(postToSave, token);
        ctx.result("User: "+requestValidationService.extractUsername(postToSave.getUser())+" did a new post");
    }

    public void createComment(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);

        Comment commentToSave = ctx.bodyValidator(Comment.class).check(
                comment -> comment.getUser() == requestValidationService.extractUserId(token),
                "You are not allowed to make a comment as another user").get();

        operationsService.createComment(commentToSave, token);
        ctx.result("User: "+requestValidationService.extractUsername(commentToSave.getUser())+" commented on a post");
    }

    public void follow(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);

        int user = ctx.queryParamAsClass("user", Integer.class).check(
                u -> u == requestValidationService.extractUserId(token),
                "Your request id does not match with your authentication id").get();
        int follows = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("follows")));

        try{
            operationsService.follow(user,follows);
            ctx.result("User: "+requestValidationService.extractUsername(user)+
                    " followed user: "+requestValidationService.extractUsername(follows));
        }catch(NullPointerException e){
            throw new BadRequestResponse("Query params are null");
        }
    }

    public void unfollow(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);

        int user = ctx.queryParamAsClass("user", Integer.class).check(
                u -> u == requestValidationService.extractUserId(token),
                "Your request id does not match with your authentication id").get();
        int unfollows = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("unfollows")));

        try{
            operationsService.unfollow(user,unfollows);
            ctx.result("User: "+requestValidationService.extractUsername(user)+
                    " unfollowed user: "+requestValidationService.extractUsername(unfollows));
        }catch(NullPointerException e){
            throw new BadRequestResponse("Query params are null");
        }
    }

    public void createUrlForPostAndComments(Context ctx) {
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = ctx.pathParamAsClass("id", Integer.class).check(
                u -> u == requestValidationService.extractUserId(token),
                "Your request id does not match with your authentication id").get();

        ArrayList<Integer> user_post_ids = requestValidationService.getUsersPostIds(user);
        int post = ctx.pathParamAsClass("post", Integer.class).check(
                user_post_ids::contains,"This post is not posted by you").get();

        String generated_url = operationsService.createUrlForPostAndComments(user, post);
        ctx.result(generated_url);
    }

    public void decodeLink(Context ctx) {
        try{
            HashMap<String, ArrayList<String>> post_and_comments = operationsService.decodeUrl(ctx.url());
            JSONObject jsonResults = new JSONObject(post_and_comments);
            ctx.result(jsonResults.toString());
        }catch (Exception e){
            throw new NotFoundResponse();
        }
    }
}
