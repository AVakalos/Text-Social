package org.apostolis.repository;

import org.apostolis.model.Comment;
import org.apostolis.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


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
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int getCountOfUserCommentsUnderThisPost(int user, int post){
        int comments_count;
        DbUtils.ThrowingFunction<Connection, Integer, Exception> get_comments_count = (conn) -> {
            int count;
            try(PreparedStatement stm = conn.prepareStatement(
                    "SELECT COUNT(*) FROM comments WHERE post_id=? and user_id=?")){
                stm.setInt(1, post);
                stm.setInt(2, user);
                ResultSet rs = stm.executeQuery();
                rs.next();
                count = rs.getInt("count");
            }
            return count;
        };
        try{
            comments_count = dbUtils.doInTransaction(get_comments_count);
            logger.info(String.valueOf(comments_count));
        }catch(Exception e){
            logger.error("Could not retrieve the comments count from database");
            throw new RuntimeException(e.getMessage());
        }
        return comments_count;
    }

    @Override
    public HashMap<String, ArrayList<String>> getPostAndNLatestComments(int post_id, int latest) {
        DbUtils.ThrowingFunction<Connection, HashMap<String,ArrayList<String>>, Exception> get_post_and_n_latest_comments = (conn) -> {
            String query = "WITH latest_comments AS(\n" +
                            "\tSELECT * FROM\n" +
                            "\tcomments\n" +
                            "\tWHERE post_id = ?\n" +
                            "\tORDER BY created DESC\n" +
                            "\tLIMIT ?\n" +
                            ")\n" +
                            "SELECT p.text AS post, c.text AS comments\n" +
                            "FROM posts p JOIN latest_comments c\n" +
                            "ON p.post_id = c.post_id";

            HashMap<String,ArrayList<String>> results = new LinkedHashMap<>();
            try(PreparedStatement stm = conn.prepareStatement(query)){
                stm.setInt(1,post_id);
                stm.setInt(2,latest);
                ResultSet rs = stm.executeQuery();
                while(rs.next()){
                    String post = rs.getString("post");
                    String comment = rs.getString("comments");
                    if (!results.containsKey(post)){
                        results.put(post, new ArrayList<>());
                    }
                    results.get(post).add(comment);
                }
            }
            return results;
        };
        try{
            return dbUtils.doInTransaction(get_post_and_n_latest_comments);
        }catch(Exception e){
            logger.error("Post "+post_id+" and latest "+latest+" comments could not be retrieved");
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteFollow(int follower, int to_unfollow) {
        DbUtils.ThrowingConsumer<Connection,Exception> deleteFollowerFromDb = (conn) -> {
            try(PreparedStatement delete_follower_stm = conn.prepareStatement(
                    "DELETE FROM followers WHERE user_id = ? AND follower_id=?")){
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
            throw new RuntimeException(e.getMessage());
        }
    }
}
