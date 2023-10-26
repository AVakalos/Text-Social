package org.apostolis.service;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;

import java.util.ArrayList;
import java.util.HashMap;

public interface OperationsService {
    void createPost(Post postToSave, String token);
    void createComment(Comment commentToSave, String token);
    void follow(int follower, int to_follow, String token);
    void unfollow(int follower, int to_unfollow, String token);
    String createUrlForPostAndComments(int user, int post, String token);
    HashMap<String, ArrayList<String>> decodeUrl(String url);
    String getUsername(int user_id);
}
