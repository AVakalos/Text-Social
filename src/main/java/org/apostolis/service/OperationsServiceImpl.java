package org.apostolis.service;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import org.apostolis.config.AppConfig;
import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.model.Role;
import org.apostolis.repository.OperationsRepository;
import org.apostolis.security.TokenManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/* THis class implements the business logic of the application's operations. */

public class OperationsServiceImpl implements OperationsService{

    private final int free_post_size;
    private final int premium_post_size;
    private final int max_comments_number;

    private final OperationsRepository operationsRepository;

    private final RequestValidationService requestValidationService;
    private final TokenManager tokenManager;

    public OperationsServiceImpl(
            OperationsRepository operationsRepository,
            TokenManager tokenManager,
            RequestValidationService requestValidationService,
            int free_post_size,
            int premium_post_size,
            int max_comments_number) {

        this.operationsRepository = operationsRepository;
        this.tokenManager = tokenManager;
        this.free_post_size = free_post_size;
        this.premium_post_size = premium_post_size;
        this.max_comments_number = max_comments_number;
        this.requestValidationService = requestValidationService;
    }


    @Override
    public void createPost(Post postToSave, String token) {

        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (postToSave.getUser() != authenticated_user_id){
            throw new UnauthorizedResponse("You are not allowed to make a post as another user");
        }

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
        //postToSave.setCreatedAt(LocalDateTime.now(AppConfig.clock));
        operationsRepository.savePost(postToSave);
    }

    @Override
    public void createComment(Comment commentToSave, String token) {

        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (commentToSave.getUser() != authenticated_user_id){
            throw new UnauthorizedResponse("You are not allowed to make a comment as another user");
        }

        if(tokenManager.extractRole(token).equals(Role.FREE)){
            int comments_count = operationsRepository.getCountOfUserCommentsUnderThisPost(
                    commentToSave.getUser(), commentToSave.getPost());

            if(comments_count >= max_comments_number){
                throw new UnauthorizedResponse("Free users can comment up to "+ max_comments_number +" times per post."+
                        "\nYou reached the maximum number of comments for this post.");
            }
        }
        //commentToSave.setCreatedAt(LocalDateTime.now(AppConfig.clock));
        operationsRepository.saveComment(commentToSave);
    }
    @Override
    public void follow(int follower, int to_follow, String token) {
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (follower != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
        if (follower != to_follow){
            operationsRepository.saveFollow(follower, to_follow);
        }else{
            throw new UnauthorizedResponse("You can't follow yourself");
        }
    }

    @Override
    public void unfollow(int follower, int to_unfollow, String token) {
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (follower != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
        operationsRepository.deleteFollow(follower, to_unfollow);
    }


    @Override
    public String createUrlForPostAndComments(int user, int post, String token) {
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if(user != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
        ArrayList<Integer> user_post_ids = requestValidationService.getUsersPostIds(user);
        if(!user_post_ids.contains(post)){
            throw new UnauthorizedResponse("You cannot create shareable link for a post of another user");
        }

        try{
            // register the link to prevent data leaks via url manipulation
            operationsRepository.registerLink(user, post);
            String description = user+","+post;

            String host = AppConfig.readProperties().getProperty("host");
            String port = AppConfig.readProperties().getProperty("port");

            return "http://"+host+":"+port+"/"+URLEncoder.encode(description, StandardCharsets.UTF_8.toString());
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public HashMap<String, ArrayList<String>> decodeUrl(String url) {
        try{
            String host = AppConfig.readProperties().getProperty("host");
            String port = AppConfig.readProperties().getProperty("port");
            String encoded = url.replace("http://"+host+":"+port+"/","");
            String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString());

            String[] splitted =  decoded.split(",");
            int user_id = Integer.parseInt(splitted[0]);
            int post_id = Integer.parseInt(splitted[1]);
            if(operationsRepository.checkLink(user_id, post_id)){
                return operationsRepository.getPostAndNLatestComments(post_id, 100);
            }else{
                throw new BadRequestResponse("The link is invalid");
            }
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public String getUsername(int user_id){
        return requestValidationService.extractUsername(user_id);
    }
}
