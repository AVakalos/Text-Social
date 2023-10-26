package org.apostolis.controller;

import io.javalin.http.Context;
import org.apostolis.service.ViewsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/* This REST controller class handles requests regarding operations that need data retrieval */

public class ViewsController {

    private final ViewsService viewsService;

    public ViewsController(ViewsService viewsService) {
        this.viewsService = viewsService;
    }

    public void getFollowersPostsInReverseChrono(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String, ArrayList<String>> results = viewsService.getFollowersPostsInReverseChrono(user, token);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts from followers or followers doesn't exist");
        }else{
            ctx.json(results);
        }
    }
    public void getOwnPostsWithLast100CommentsInReverseChrono(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String, ArrayList<String>> results =
                viewsService.getOwnPostsWithLastNCommentsInReverseChrono(user, 100, token);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts");
        }else{
            ctx.json(results);
        }
    }
    public void getAllCommentsOnOwnPosts(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String,ArrayList<String>> results = viewsService.getAllCommentsOnOwnPosts(user, token);
        if(results == null || results.isEmpty()){
            ctx.result("There are no comments in any post");
        }else{
            ctx.json(results);
        }
    }
    public void getLatestCommentsOnOwnOrFollowersPosts(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String, String> results = viewsService.getLatestCommentsOnOwnOrFollowersPosts(user, token);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts");
        }else{
            ctx.json(results);
        }
    }
    public void getFollowersOf(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = Integer.parseInt(ctx.pathParam("id"));
        ArrayList<String> results = viewsService.getFollowersOf(user,token);
        if(results == null || results.isEmpty()){
            ctx.result("The user has no followers");
        }else{
            ctx.json(results);
        }
    }
    public void getUsersToFollow(Context ctx){
        String token = Objects.requireNonNull(ctx.header("Authorization")).substring(7);
        int user = Integer.parseInt(ctx.pathParam("id"));
        ArrayList<String> results = viewsService.getUsersToFollow(user,token);
        if(results == null || results.isEmpty()){
            ctx.result("There are no users to follow");
        }else{
            ctx.json(results);
        }
    }
}
