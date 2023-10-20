package org.apostolis.service;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;

import java.util.ArrayList;
import java.util.HashMap;

public interface OperationsService {
    void createPost(Post postToSave, String token);
    void createComment(Comment commentToSave, String token);
    void follow(int follower, int to_follow);
    void unfollow(int follower, int to_unfollow);
    String createUrlForPostAndComments(int user, int post);
    HashMap<String, ArrayList<String>> decodeUrl(String url);
}
