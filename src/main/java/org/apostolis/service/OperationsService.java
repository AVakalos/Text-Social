package org.apostolis.service;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.model.Role;

public interface OperationsService {

    void create_post(Post postToSave, String token);

    void create_comment(Comment commentToSave, String token);

    void follow(int follower, int to_follow);

    void unfollow(int follower, int to_unfollow);

    //Role check_Authorization(String token);
}
