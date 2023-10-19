package org.apostolis.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ViewsRepositoryImpl implements ViewsRepository{

    private static final Logger logger = LoggerFactory.getLogger(ViewsRepositoryImpl.class);
    private final DbUtils dbUtils;

    public ViewsRepositoryImpl(DbUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    @Override
    public HashMap<String, ArrayList<String>> get_followers_posts_in_reverse_chrono(int id) {
        DbUtils.ThrowingFunction<Connection, HashMap<String, ArrayList<String>>, Exception> get_followers_posts = (conn) -> {
            String query = "SELECT username, text FROM " +
                        "    users u " +
                        "    INNER JOIN " +
                        "    (SELECT text, user_id, created FROM posts p " +
                        "    INNER JOIN " +
                        "    (SELECT follower_id FROM followers WHERE user_id = ?) f " +
                        "    ON p.user_id = f.follower_id) posts " +
                        "    ON u.user_id = posts.user_id " +
                        "    ORDER BY created DESC";

            HashMap<String, ArrayList<String>> results = new LinkedHashMap<>();
            try(PreparedStatement pst = conn.prepareStatement(query)){
                pst.setInt(1,id);
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    String follower = rs.getString("username");
                    if (!results.containsKey(follower)){
                        results.put(follower,new ArrayList<>());
                    }
                    results.get(follower).add(rs.getString("text"));
                }
                return results;
            }
        };
        try{
            return dbUtils.doInTransaction(get_followers_posts);
        }catch(Exception e){
            logger.error("Retrieving followers post failed for user: "+id);
        }
        return null;
    }

    @Override
    public HashMap<String, ArrayList<String>> get_own_posts_with_last_n_comments_in_reverse_chrono(int id, int max_latest_comments) {
        DbUtils.ThrowingFunction<Connection, HashMap<String, ArrayList<String>>, Exception> get_posts_with_comments = (conn) -> {
            String query = "WITH numbered_comments AS" +
                    "    (SELECT *, " +
                    "            row_number() over ( " +
                    "                PARTITION BY post_id " +
                    "                ORDER BY created DESC " +
                    "            ) AS row_number " +
                    "    FROM comments" +
                    ")," +
                    "last_n_comments AS ( " +
                    "    SELECT * " +
                    "    FROM numbered_comments " +
                    "    WHERE numbered_comments.row_number <= ? " +
                    "), " +
                    "user_posts AS( " +
                    "    SELECT * FROM " +
                    "        posts " +
                    "    WHERE user_id = ? " +
                    ")" +
                    "SELECT p.text post, c.text comment " +
                    "FROM last_n_comments AS c " +
                    "RIGHT OUTER JOIN user_posts AS p " +
                    "    ON c.post_id = p.post_id " +
                    "ORDER BY p.created DESC";
            HashMap<String, ArrayList<String>> results = new LinkedHashMap<>();
            try(PreparedStatement pst = conn.prepareStatement(query)){
                pst.setInt(1, max_latest_comments);
                pst.setInt(2, id);
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    String post = rs.getString("post");
                    if (!results.containsKey(post)) {
                        results.put(post, new ArrayList<>());
                    }
                    String comment = rs.getString("comment");
                    if(comment!=null){
                        results.get(post).add(comment);
                    }
                }
                return results;
            }
        };
        try{
            return dbUtils.doInTransaction(get_posts_with_comments);
        }catch(Exception e){
            logger.error("No posts for user: "+id);
        }
        return null;
    }

    @Override
    public ArrayList<String> get_all_comments_on_own_posts(int id) {
        DbUtils.ThrowingFunction<Connection, ArrayList<String>, Exception> get_all_comments = (conn) -> {
            String query = "SELECT c.text AS comment " +
                            "FROM comments AS c " +
                            "RIGHT OUTER JOIN " +
                            "(SELECT text, post_id FROM posts WHERE user_id = ?) AS p " +
                            "ON p.post_id = c.post_id";
            ArrayList<String> results = new ArrayList<>();
            try(PreparedStatement pst = conn.prepareStatement(query)){
                pst.setInt(1,id);
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    String comment = rs.getString("comment");
                    if(comment != null){
                        results.add(comment);
                    }
                }
                return results;
            }
        };
        try{
            return dbUtils.doInTransaction(get_all_comments);
        }catch(Exception e){
            logger.error("No comments in any post for user: "+id);
        }
        return null;
    }

    @Override
    public HashMap<String, String> get_latest_comments_on_own_or_followers_posts(int id) {
        DbUtils.ThrowingFunction<Connection, HashMap<String, String>, Exception> latest_comments = (conn) -> {
            String query = "WITH numbered_comments AS " +
                    "    (SELECT *, " +
                    "            row_number() over ( " +
                    "                PARTITION BY post_id " +
                    "                order by created DESC " +
                    "            ) AS row_number " +
                    "    FROM comments " +
                    "), " +
                    "last_comments AS ( " +
                    "    SELECT * " +
                    "    FROM numbered_comments " +
                    "    WHERE numbered_comments.row_number = 1 " +
                    "), " +
                    "user_and_follower_posts AS( " +
                    "    SELECT * FROM " +
                    "        posts " +
                    "    WHERE user_id in ( " +
                    "        SELECT follower_id " +
                    "        FROM followers " +
                    "        WHERE user_id = ?) " +
                    "       or user_id = ? " +
                    ") " +
                    "SELECT p.text AS post, c.text AS latest_comment " +
                    "FROM last_comments AS c " +
                    "JOIN user_and_follower_posts AS p " +
                    "    ON c.post_id = p.post_id";
            HashMap<String, String> results = new LinkedHashMap<>();
            try(PreparedStatement pst = conn.prepareStatement(query)){
                pst.setInt(1,id);
                pst.setInt(2,id);
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    results.put(rs.getString("post"),rs.getString("latest_comment"));
                }
            }
            return results;
        };
        try{
            return dbUtils.doInTransaction(latest_comments);
        }catch(Exception e){
            logger.error("No comments in any post (own or follower's) for user: "+id);
        }
        return null;
    }

    @Override
    public ArrayList<String> get_followers_of(int id) {
        DbUtils.ThrowingFunction<Connection, ArrayList<String>, Exception> get_followers = (conn) -> {
            String query = "WITH follower_ids AS (SELECT follower_id " +
                            "   FROM followers " +
                            "   WHERE user_id = ?) " +
                            "SELECT username AS followers " +
                            "FROM users " +
                            "INNER JOIN follower_ids AS f " +
                            "ON user_id = f.follower_id;";
            ArrayList<String> results = new ArrayList<>();
            try(PreparedStatement pst = conn.prepareStatement(query)){
                pst.setInt(1,id);
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    String comment = rs.getString("followers");
                    if(comment != null){
                        results.add(comment);
                    }
                }
                return results;
            }
        };
        try{
            return dbUtils.doInTransaction(get_followers);
        }catch(Exception e){
            logger.error("No followers for user: "+id);
        }
        return null;
    }

    @Override
    public ArrayList<String> get_users_to_follow(int id) {
        DbUtils.ThrowingFunction<Connection, ArrayList<String>, Exception> get_users_to_follow = (conn) -> {
            String query = "SELECT username AS to_follow " +
                            "FROM users " +
                            "WHERE user_id != ? " +
                            "AND users.user_id NOT IN " +
                            "    (SELECT follower_id " +
                            "     FROM followers " +
                            "     WHERE user_id = ?);";
            ArrayList<String> results = new ArrayList<>();
            try(PreparedStatement pst = conn.prepareStatement(query)){
                pst.setInt(1,id);
                pst.setInt(2,id);
                ResultSet rs = pst.executeQuery();
                while(rs.next()){
                    String comment = rs.getString("to_follow");
                    if(comment != null){
                        results.add(comment);
                    }
                }
                return results;
            }
        };
        try{
            return dbUtils.doInTransaction(get_users_to_follow);
        }catch(Exception e){
            logger.error("No users to follow for user: "+id);
        }
        return null;
    }
}
