package org.apostolis.service;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import jakarta.validation.*;
import org.apostolis.config.AppConfig;
import org.apostolis.model.*;
import org.apostolis.repository.OperationsRepository;
import org.apostolis.security.TokenManager;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        try {
            ValidationUtils.validateInput(postToSave);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }

        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (postToSave.user() != authenticated_user_id){
            throw new UnauthorizedResponse("You are not allowed to make a post as another user");
        }

        Role authlevel = tokenManager.extractRole(token);
        int post_size = postToSave.text().length();
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
    public void createComment(Comment commentToSave, String token) {

        try {
            ValidationUtils.validateInput(commentToSave);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (commentToSave.user() != authenticated_user_id){
            throw new UnauthorizedResponse("You are not allowed to make a comment as another user");
        }

        if(tokenManager.extractRole(token).equals(Role.FREE)){
            int comments_count = operationsRepository.getCountOfUserCommentsUnderThisPost(
                    commentToSave.user(), commentToSave.post());

            if(comments_count >= max_comments_number){
                throw new UnauthorizedResponse("Free users can comment up to "+ max_comments_number +" times per post."+
                        "\nYou reached the maximum number of comments for this post.");
            }
        }
        operationsRepository.saveComment(commentToSave);
    }
    @Override
    public void follow(FollowRequest followToSave, String token) {
        try {
            ValidationUtils.validateInput(followToSave);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (followToSave.user() != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
        if (followToSave.user() != followToSave.follows()){
            operationsRepository.saveFollow(followToSave.user(), followToSave.follows());
        }else{
            throw new UnauthorizedResponse("You can't follow yourself");
        }
    }

    @Override
    public void unfollow(UnfollowRequest unfollowToSave, String token) {
        try {
            ValidationUtils.validateInput(unfollowToSave);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if (unfollowToSave.user() != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
        operationsRepository.deleteFollow(unfollowToSave.user(), unfollowToSave.unfollows());
    }


    @Override
    public String createUrlForPostAndComments(CreateLinkRequest createLinkRequest, String token) {
        try {
            ValidationUtils.validateInput(createLinkRequest);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        int authenticated_user_id = requestValidationService.extractUserId(token);
        int user = createLinkRequest.user();
        int post = createLinkRequest.post();
        if(user != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
        ArrayList<Integer> user_post_ids = requestValidationService.getUsersPostIds(user);
        if(!user_post_ids.contains(post)){
            throw new UnauthorizedResponse("You cannot create shareable link for a post of another user");
        }

        // register the link to prevent data leaks via url manipulation
        operationsRepository.registerLink(user, post);
        String description = user+","+post;

        String host = AppConfig.readProperties().getProperty("host");
        String port = AppConfig.readProperties().getProperty("port");

        return "http://"+host+":"+port+"/"+URLEncoder.encode(description, StandardCharsets.UTF_8);
    }
    @Override
    public HashMap<String, ArrayList<String>> decodeUrl(DecodeRequest decodeRequest) {
        try {
            ValidationUtils.validateInput(decodeRequest);
        }catch(ConstraintViolationException c){
            throw new BadRequestResponse(c.getMessage());
        }
        String host = AppConfig.readProperties().getProperty("host");
        String port = AppConfig.readProperties().getProperty("port");
        String encoded = decodeRequest.url().replace("http://"+host+":"+port+"/","");
        String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);

        String[] splitted =  decoded.split(",");
        int user_id = Integer.parseInt(splitted[0]);
        int post_id = Integer.parseInt(splitted[1]);
        if(operationsRepository.checkLink(user_id, post_id)){
            return operationsRepository.getPostAndNLatestComments(post_id, 100);
        }else{
            throw new BadRequestResponse("The link is invalid");
        }
    }

    @Override
    public String getUsername(int user_id){
        return requestValidationService.extractUsername(user_id);
    }
}
