package org.apostolis.repository;

import java.util.ArrayList;
import java.util.HashMap;

public interface ViewsRepository {
    HashMap<String, ArrayList<String>> getFollowersPostsInReverseChrono(int id);
    HashMap<String,ArrayList<String>> getOwnPostsWithLastNCommentsInReverseChrono(int id, int max_latest_comments);
    HashMap<String,ArrayList<String>> getAllCommentsOnOwnPosts(int id);
    HashMap<String,String> getLatestCommentsOnOwnOrFollowersPosts(int id);
    ArrayList<String> getFollowersOf(int id);
    ArrayList<String> getUsersToFollow(int id);
}
