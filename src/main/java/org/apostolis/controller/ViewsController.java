package org.apostolis.controller;

import io.javalin.http.Context;
import org.apostolis.repository.ViewsRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewsController {

    private final ViewsRepository viewsRepository;

    public ViewsController(ViewsRepository viewsRepository) {
        this.viewsRepository = viewsRepository;
    }

    public void get_followers_posts_in_reverse_chrono(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String, ArrayList<String>> results = viewsRepository.get_followers_posts_in_reverse_chrono(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts from followers or followers doesn't exist");
        }else{
            ctx.json(results);
        }
    }
    public void get_own_posts_with_last_100_comments_in_reverse_chrono(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String, ArrayList<String>> results =
                viewsRepository.get_own_posts_with_last_n_comments_in_reverse_chrono(user, 100);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts");
        }else{
            ctx.json(results);
        }
    }
    public void get_all_comments_on_own_posts(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        ArrayList<String> results = viewsRepository.get_all_comments_on_own_posts(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no comments in any post");
        }else{
            ctx.json(results);
        }
    }
    public void get_latest_comments_on_own_or_followers_posts(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        HashMap<String, String> results = viewsRepository.get_latest_comments_on_own_or_followers_posts(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no posts");
        }else{
            ctx.json(results);
        }
    }
    public void get_followers_of(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        ArrayList<String> results = viewsRepository.get_followers_of(user);
        if(results == null || results.isEmpty()){
            ctx.result("The user has no followers");
        }else{
            ctx.json(results);
        }
    }
    public void get_users_to_follow(Context ctx){
        int user = Integer.parseInt(ctx.pathParam("id"));
        ArrayList<String> results = viewsRepository.get_users_to_follow(user);
        if(results == null || results.isEmpty()){
            ctx.result("There are no users to follow");
        }else{
            ctx.json(results);
        }
    }
}
