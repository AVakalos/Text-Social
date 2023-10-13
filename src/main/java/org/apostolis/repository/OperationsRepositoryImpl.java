package org.apostolis.repository;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class OperationsRepositoryImpl implements OperationsRepository {

    private final DbUtils dbUtils;

    private static final Logger logger = LoggerFactory.getLogger(OperationsRepositoryImpl.class);

    public OperationsRepositoryImpl(DbUtils dbUtils){
        this.dbUtils = dbUtils;
    }


    @Override
    public void savePost(Post postToSave) {
        DbUtils.ThrowingConsumer<Connection,Exception> savePostIntoDb = (conn) -> {
            try(PreparedStatement savepost_stm = conn.prepareStatement(
                "INSERT INTO posts (user_id, text, created) VALUES (?,?,?)")){
                savepost_stm.setInt(1,postToSave.getUser());
                savepost_stm.setString(2, postToSave.getText());
                savepost_stm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                savepost_stm.executeUpdate();
            }
        };
        try{
            dbUtils.doInTransaction(savePostIntoDb);
            logger.info("Post saved successfully in the database.");
        }catch (Exception e){
            logger.error("Post didn't saved.");
        }
    }

    @Override
    public void saveComment(Comment commentToSave) {
        DbUtils.ThrowingConsumer<Connection,Exception> saveCommentIntoDb = (conn) -> {
            try(PreparedStatement savecomment_stm = conn.prepareStatement(
                    "INSERT INTO comments (post_id, user_id, text, created) VALUES (?,?,?,?)")){
                savecomment_stm.setInt(1,commentToSave.getPost());
                savecomment_stm.setInt(2,commentToSave.getUser());
                savecomment_stm.setString(3, commentToSave.getText());
                savecomment_stm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                savecomment_stm.executeUpdate();
            }
        };
        try{
            dbUtils.doInTransaction(saveCommentIntoDb);
            logger.info("Comment saved successfully in the database.");
        }catch (Exception e){
            logger.error("Comment didn't saved.");
        }
    }

    @Override
    public void saveFollow(int follower, int to_follow) {
        DbUtils.ThrowingConsumer<Connection,Exception> saveFollowIntoDb = (conn) -> {
            try(PreparedStatement savefollow_stm = conn.prepareStatement("INSERT INTO followers VALUES (?,?)")){
                savefollow_stm.setInt(1,follower);
                savefollow_stm.setInt(2,to_follow);
                savefollow_stm.executeUpdate();
            }
        };
        try{
            dbUtils.doInTransaction(saveFollowIntoDb);
            logger.info("Follow saved successfully in the database.");
        }catch (Exception e){
            logger.error("Follow didn't saved.");
        }
    }

    @Override
    public void deleteFollow(int follower, int to_unfollow) {
        DbUtils.ThrowingConsumer<Connection,Exception> deleteFollowerFromDb = (conn) -> {
            try(PreparedStatement delete_follower_stm = conn.prepareStatement(
                    "DELETE FROM followers WHERE user_id=? AND follower_id=?")){
                delete_follower_stm.setInt(1,follower);
                delete_follower_stm.setInt(2,to_unfollow);
                delete_follower_stm.executeUpdate();
            }
        };
        try{
            dbUtils.doInTransaction(deleteFollowerFromDb);
            logger.info("Follow deleted successfully from database.");
        }catch (Exception e){
            logger.error("Follow didn't deleted.");
        }
    }
}
