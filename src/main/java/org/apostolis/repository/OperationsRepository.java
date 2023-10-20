package org.apostolis.repository;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;

import java.util.ArrayList;
import java.util.HashMap;

public interface OperationsRepository {
    void savePost(Post postToSave);
    void saveComment(Comment commentToSave);
    void saveFollow(int follower,int to_follow);
    void deleteFollow(int follower, int to_unfollow);
    int getCountOfUserCommentsUnderThisPost(int user, int post);
    HashMap<String, ArrayList<String>> getPostAndNLatestComments(int post_id, int latest);
    ArrayList<Integer> getPostIds(int user_id);
    void registerLink(int user, int post);
    boolean checkLink(int user, int post);
}
