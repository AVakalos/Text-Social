package org.apostolis.service;

import org.apostolis.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public interface OperationsService {
    void createPost(Post postToSave, String token);
    void createComment(Comment commentToSave, String token);
    void follow(FollowRequest followToSave, String token);

    void unfollow(UnfollowRequest unfollowToSave, String token);
    String createUrlForPostAndComments(CreateLinkRequest createLinkRequest, String token);
    HashMap<String, ArrayList<String>> decodeUrl(DecodeRequest decodeRequest);
    String getUsername(int user_id);
}
