package org.apostolis.service;

import io.javalin.http.UnauthorizedResponse;
import org.apostolis.repository.ViewsRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewsServiceImpl implements ViewsService{
    private final ViewsRepository viewsRepository;

    private final RequestValidationService requestValidationService;


    public ViewsServiceImpl(ViewsRepository viewsRepository, RequestValidationService requestValidationService) {
        this.viewsRepository = viewsRepository;
        this.requestValidationService = requestValidationService;
    }


    private void checkUserAuthentication(int user_id, String token){
        int authenticated_user_id = requestValidationService.extractUserId(token);
        if(user_id != authenticated_user_id){
            throw new UnauthorizedResponse("Your request id does not match with your authentication id");
        }
    }

    @Override
    public HashMap<String, ArrayList<String>> getFollowersPostsInReverseChrono(int user_id, String token) {
        checkUserAuthentication(user_id, token);
        return viewsRepository.getFollowersPostsInReverseChrono(user_id);
    }

    @Override
    public HashMap<String, ArrayList<String>> getOwnPostsWithLastNCommentsInReverseChrono(int user_id, int max_latest_comments, String token) {
        checkUserAuthentication(user_id, token);
        return viewsRepository.getOwnPostsWithLastNCommentsInReverseChrono(user_id, max_latest_comments);
    }

    @Override
    public HashMap<String, ArrayList<String>> getAllCommentsOnOwnPosts(int user_id, String token) {
        checkUserAuthentication(user_id, token);
        return viewsRepository.getAllCommentsOnOwnPosts(user_id);
    }

    @Override
    public HashMap<String, String> getLatestCommentsOnOwnOrFollowersPosts(int user_id, String token) {
        checkUserAuthentication(user_id, token);
        return viewsRepository.getLatestCommentsOnOwnOrFollowersPosts(user_id);
    }

    @Override
    public ArrayList<String> getFollowersOf(int user_id, String token) {
        checkUserAuthentication(user_id, token);
        return viewsRepository.getFollowersOf(user_id);
    }

    @Override
    public ArrayList<String> getUsersToFollow(int user_id, String token) {
        checkUserAuthentication(user_id, token);
        return viewsRepository.getUsersToFollow(user_id);
    }
}
