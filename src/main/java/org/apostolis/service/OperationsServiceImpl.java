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

    private int free_post_size = 1000;
    private int premium_post_size = 3000;
    private int max_comments_number = 5;

    private static final Logger logger = LoggerFactory.getLogger(OperationsServiceImpl.class);

    private final OperationsRepository operationsRepository;
    private final TokenManager tokenManager;

    public OperationsServiceImpl(OperationsRepository operationsRepository, TokenManager tokenManager) {
        this.operationsRepository = operationsRepository;
        this.tokenManager = tokenManager;
    }

    public OperationsServiceImpl(OperationsRepository operationsRepository,
                                 TokenManager tokenManager,
                                 int free_post_size,
                                 int premium_post_size,
                                 int max_comments_number) {
        this.operationsRepository = operationsRepository;
        this.tokenManager = tokenManager;
        this.free_post_size = free_post_size;
        this.premium_post_size = premium_post_size;
        this.max_comments_number = max_comments_number;
    }


    @Override
    public void create_post(Post postToSave, String token) {
        Role authlevel = check_Authorization(token);
        int post_size = postToSave.getText().length();
        switch (authlevel){
            case FREE:
                if(post_size > free_post_size){
                    throw new UnauthorizedResponse("Free users can post texts up to "+ free_post_size +" characters."+
                            "\nYour post was "+post_size+" characters");
                }
                break;
            case PREMIUM:
                if(post_size > premium_post_size){
                    throw new UnauthorizedResponse("Premium users can post texts up to "+ premium_post_size +" characters."+
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
                        "SELECT COUNT(*) FROM comments WHERE post_id=? and user_id=?")){
                    stm.setInt(1,commentToSave.getPost());
                    stm.setInt(2,commentToSave.getUser());
                    System.out.println(stm);
                    ResultSet rs = stm.executeQuery();

                    while(rs.next()){
                        count = rs.getInt("count");
                        System.out.println(count);
                    }
                }
                return count;
            };
            try{
                comments_count = operationsRepository.getConnection().doInTransaction(get_comments_count);
                logger.info(String.valueOf(comments_count));
            }catch(Exception e){
                logger.error("Could not retrieve the comments count from database");
                throw new RuntimeException(e.getMessage());
            }
            if(comments_count >= max_comments_number){
                throw new UnauthorizedResponse("Free users can comment up to "+ max_comments_number +" times per post."+
                        "\nYou reached the maximum number of comments for this post.");
            }
        }
        operationsRepository.saveComment(commentToSave);
    }
    @Override
    public void follow(int follower, int to_follow) {
        if (follower != to_follow){
            operationsRepository.saveFollow(follower, to_follow);
        }else{
            throw new UnauthorizedResponse("You can't follow yourself");
        }

    }

    @Override
    public void unfollow(int follower, int to_unfollow) {
        operationsRepository.deleteFollow(follower, to_unfollow);
    }

    private Role check_Authorization(String token){
        return tokenManager.extractRole(token);
    }

}
