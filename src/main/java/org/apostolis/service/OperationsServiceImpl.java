package org.apostolis.service;

import io.javalin.http.UnauthorizedResponse;
import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.apostolis.model.Role;
import org.apostolis.repository.DbUtils;
import org.apostolis.repository.OperationsRepository;
import org.apostolis.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OperationsServiceImpl implements OperationsService{

    private final int FREE_POST_SIZE = 1000;
    private final int PREMIUM_POST_SIZE = 3000;
    private final int MAX_COMMENTS_NUM = 5;

    private static final Logger logger = LoggerFactory.getLogger(OperationsServiceImpl.class);

    private final OperationsRepository operationsRepository;
    private final TokenManager tokenManager;

    public OperationsServiceImpl(OperationsRepository operationsRepository, TokenManager tokenManager) {
        this.operationsRepository = operationsRepository;
        this.tokenManager = tokenManager;
    }

    @Override
    public void create_post(Post postToSave, String token) {
        Role authlevel = check_Authorization(token);
        int post_size = postToSave.getText().length();
        switch (authlevel){
            case FREE:
                if(post_size > FREE_POST_SIZE){
                    throw new UnauthorizedResponse("Free users can post texts up to "+FREE_POST_SIZE+" characters."+
                            "\nYour post was "+post_size+" characters");
                }
                break;
            case PREMIUM:
                if(post_size > PREMIUM_POST_SIZE){
                    throw new UnauthorizedResponse("Premium users can post texts up to "+PREMIUM_POST_SIZE+" characters."+
                            "\nYour post was "+post_size+" characters");
                }
                break;
            default:
                break;
        }
        operationsRepository.savePost(postToSave);
    }

    @Override
    public void create_comment(Comment commentToSave, String token) {
        Role authlevel = check_Authorization(token);
        int comments_count = -1;
        if(authlevel.equals(Role.FREE)){
            DbUtils.ThrowingFunction<Connection, Integer, Exception> get_comments_count = (conn) -> {
                int count = -1;
                try(PreparedStatement stm = conn.prepareStatement(
                        "SELECT COUNT(*) FROM comments c WHERE c.post_id=? and c.user_id=?")){
                    stm.setInt(1,commentToSave.getPost());
                    stm.setInt(2,commentToSave.getUser());
                    ResultSet rs = stm.executeQuery();
                    while(rs.next()){
                        count = rs.getInt("count");
                    }
                }
                return count;
            };
            try{
                comments_count = (new DbUtils()).doInTransaction(get_comments_count);
            }catch(Exception e){
                logger.error(e.getMessage());
            }
            if(comments_count == MAX_COMMENTS_NUM){
                throw new UnauthorizedResponse("Free users can comment up to "+MAX_COMMENTS_NUM+" times per post."+
                        "\nYou reached the maximum number of comments for this post.");
            }
        }
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
    private Role check_Authorization(String token){
        return tokenManager.extractRole(token);
    }
}
