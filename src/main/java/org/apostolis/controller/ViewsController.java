package org.apostolis.controller;

import io.javalin.http.Context;
import org.apostolis.repository.ViewsRepository;
import org.apostolis.service.RequestValidationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/* This REST controller class handles requests regarding operations that need data retrieval */

public class ViewsController {

    private final ViewsRepository viewsRepository;

    private final RequestValidationService requestValidationService;

    public ViewsController(ViewsRepository viewsRepository, RequestValidationService requestValidationService) {
        this.viewsRepository = viewsRepository;
        this.requestValidationService = requestValidationService;
    }

    // Security: Checking if the user performing the request is the same who is logged in.
    int checkUser(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        return ctx.pathParamAsClass("id", Integer.class).check(
                u -> u == requestValidationService.extractUserId(token),
                "Your request id does not match with your authentication id").get();
    }

    public void getFollowersPostsInReverseChrono(Context ctx){
        int user = checkUser(ctx);
        HashMap<String, ArrayList<String>> results = viewsRepository.getFollowersPostsInReverseChrono(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts from followers or followers doesn't exist");
        }else{
            ctx.json(results);
        }
    }
    public void getOwnPostsWithLast100CommentsInReverseChrono(Context ctx){
        int user = checkUser(ctx);
        HashMap<String, ArrayList<String>> results =
                viewsRepository.getOwnPostsWithLastNCommentsInReverseChrono(user, 100);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts");
        }else{
            ctx.json(results);
        }
    }
    public void getAllCommentsOnOwnPosts(Context ctx){
        int user = checkUser(ctx);
        HashMap<String,ArrayList<String>> results = viewsRepository.getAllCommentsOnOwnPosts(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no comments in any post");
        }else{
            ctx.json(results);
        }
    }
    public void getLatestCommentsOnOwnOrFollowersPosts(Context ctx){
        int user = checkUser(ctx);
        HashMap<String, String> results = viewsRepository.getLatestCommentsOnOwnOrFollowersPosts(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts");
        }else{
            ctx.json(results);
        }
    }
    public void getFollowersOf(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        ArrayList<String> results = viewsRepository.getFollowersOf(user);
        if(results == null || results.isEmpty()){
            ctx.result("The user has no followers");
        }else{
            ctx.json(results);
        }
    }
    public void getUsersToFollow(Context ctx){
        int user = checkUser(ctx);
        ArrayList<String> results = viewsRepository.getUsersToFollow(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no users to follow");
        }else{
            ctx.json(results);
        }
    }
}
