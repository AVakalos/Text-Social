package org.apostolis.service;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewsService {

    HashMap<String, ArrayList<String>> getFollowersPostsInReverseChrono(int user_id, String token);
    HashMap<String,ArrayList<String>> getOwnPostsWithLastNCommentsInReverseChrono(int user_id, int max_latest_comments, String token);
    HashMap<String,ArrayList<String>> getAllCommentsOnOwnPosts(int user_id, String token);
    HashMap<String,String> getLatestCommentsOnOwnOrFollowersPosts(int user_id, String token);
    ArrayList<String> getFollowersOf(int user_id, String token);
    ArrayList<String> getUsersToFollow(int user_id, String token);
}
