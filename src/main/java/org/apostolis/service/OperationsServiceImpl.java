package org.apostolis.service;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.model.Role;
import org.apostolis.repository.OperationsRepository;
import org.apostolis.security.TokenManager;

public class OperationsServiceImpl implements OperationsService{

    private OperationsRepository operationsRepository;

    private TokenManager tokenManager;

    public OperationsServiceImpl(OperationsRepository operationsRepository, TokenManager tokenManager) {
        this.operationsRepository = operationsRepository;
        this.tokenManager = tokenManager;
    }

    @Override
    public void create_post(Post postToSave) {
        operationsRepository.savePost(postToSave);
    }

    @Override
    public void create_comment(Comment commentToSave) {
        operationsRepository.saveComment(commentToSave);

    }

    @Override
    public void follow(int follower, int to_follow) {
        operationsRepository.saveFollow(follower, to_follow);
    }

    @Override
    public void unfollow(int follower, int to_unfollow) {
        operationsRepository.deleteFollow(follower, to_unfollow);
    }

    @Override
    public Role check_Authorization(String token){
        String role = tokenManager.extractRole(token);
        return Role.valueOf(role);
    }
}
