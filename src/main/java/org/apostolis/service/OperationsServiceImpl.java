package org.apostolis.service;

import io.javalin.http.UnauthorizedResponse;
import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.model.Role;
import org.apostolis.repository.OperationsRepository;
import org.apostolis.security.TokenManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationsServiceImpl implements OperationsService{

    private final int free_post_size;
    private final int premium_post_size;
    private final int max_comments_number;

    private static final Logger logger = LoggerFactory.getLogger(OperationsServiceImpl.class);

    private final OperationsRepository operationsRepository;
    private final TokenManager tokenManager;

    public OperationsServiceImpl(OperationsRepository operationsRepository,
                                 TokenManager tokenManager,
                                 int free_post_size,
                                 int premium_post_size,
                                 int max_comments_number) {
        this.operationsRepository = operationsRepository;
        this.tokenManager = tokenManager;
        this.free_post_size = free_post_size;
        this.premium_post_size = premium_post_size;
        this.max_comments_number = max_comments_number;
    }


    @Override
    public void create_post(Post postToSave, String token) {
        Role authlevel = tokenManager.extractRole(token);
        int post_size = postToSave.getText().length();
        switch (authlevel){
            case FREE:
                if(post_size > free_post_size){
                    throw new UnauthorizedResponse("Free users can post texts up to "+ free_post_size +" characters."+
                            "\nYour post was "+post_size+" characters");
                }
                break;
            case PREMIUM:
                if(post_size > premium_post_size){
                    throw new UnauthorizedResponse("Premium users can post texts up to "+ premium_post_size +" characters."+
                            "\nYour post was "+post_size+" characters");
                }
                break;
            default:
                break;
        }
        operationsRepository.savePost(postToSave);
    }

    @Override
    public void create_comment(Comment commentToSave, String token) {

        if(tokenManager.extractRole(token).equals(Role.FREE)){
            int comments_count = operationsRepository.getCountOfUserCommentsUnderThisPost(
                    commentToSave.getUser(), commentToSave.getPost());

            if(comments_count >= max_comments_number){
                throw new UnauthorizedResponse("Free users can comment up to "+ max_comments_number +" times per post."+
                        "\nYou reached the maximum number of comments for this post.");
            }
        }
        operationsRepository.saveComment(commentToSave);
    }
    @Override
    public void follow(int follower, int to_follow) {
        if (follower != to_follow){
            operationsRepository.saveFollow(follower, to_follow);
        }else{
            throw new UnauthorizedResponse("You can't follow yourself");
        }

    }

    @Override
    public void unfollow(int follower, int to_unfollow) {
        operationsRepository.deleteFollow(follower, to_unfollow);
    }


    @Override
    public String create_url_for_post_and_comments(int user, int post) {
        try{
            String description = user+","+post;
            return "http://localhost:7777/"+URLEncoder.encode(description, StandardCharsets.UTF_8.toString());
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e.getMessage());
        }

    }
    @Override
    public String decode_url(String url) {
        try{
            String encoded = url.replace("http://localhost:7777/","");
            String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString());

            int post_id = Integer.parseInt(decoded.split(",")[1]);

            HashMap<String, ArrayList<String>> post_and_comments = operationsRepository.getPostAndNLatestComments(post_id, 100);
            JSONObject jsonResults = new JSONObject(post_and_comments);
            return jsonResults.toString();

        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
