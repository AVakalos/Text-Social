package org.apostolis.repository;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;

public interface OperationsRepository {
    void savePost(Post postToSave);

    void saveComment(Comment commentToSave);

    void saveFollow(int follower,int to_follow);

    void deleteFollow(int follower, int to_unfollow);
}
