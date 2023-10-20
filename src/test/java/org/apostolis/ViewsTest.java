package org.apostolis;

import org.apostolis.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/* Integration tests for the Views Repository Layer. */

public class ViewsTest {
    private static DbUtils testDbUtils;
    private static ViewsRepository testViewsRepository;
    private static final String testUrl = "jdbc:postgresql://localhost:5433/TextSocialTest";
    private static final String user = "postgres";
    private static final String password = "1234";

    private static final Logger logger = LoggerFactory.getLogger(ViewsTest.class);

    @BeforeAll
    static void setup(){
        testDbUtils = new DbUtils(testUrl, user, password);
        testViewsRepository = new ViewsRepositoryImpl(testDbUtils);

        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            // initial clean for any possible garbage data
            try(PreparedStatement clean_stm = connection.prepareStatement(
                    "TRUNCATE TABLE users,comments, posts, followers RESTART IDENTITY CASCADE")) {
                clean_stm.executeUpdate();
            }

            // Populate users table
            String insert_users = "INSERT INTO users (username,password,role) VALUES" +
                                        "('user1','pass','role')," +
                                        "('user2','pass','role')," +
                                        "('user3','pass','role')";
            try(PreparedStatement insert_users_stm = connection.prepareStatement(insert_users)){
                insert_users_stm.executeUpdate();
            }

            // Populate posts table (post_id is autoincrement 1,2,3,4 accordingly)
            String insert_posts = "INSERT INTO posts (user_id, text, created) VALUES " +
                                        "(1,'post1 from user1',?)," +
                                        "(1,'post2 from user1',?)," +
                                        "(2,'post1 from user2',?)," +
                                        "(3,'post1 from user3',?)";
            try(PreparedStatement insert_posts_stm = connection.prepareStatement(insert_posts)){
                for(int i=1; i<=4; i++){
                    insert_posts_stm.setTimestamp(i, Timestamp.from(
                            new Timestamp(System.currentTimeMillis()).toInstant().plusSeconds(i*30)));
                }
                insert_posts_stm.executeUpdate();
            }

            // Populate comments table
            String insert_comments = "INSERT INTO comments (post_id,user_id,text,created) VALUES" +
                                        "(1,2,'com1 from user2',?)," +
                                        "(2,1,'com1 from user1',?)," +
                                        "(3,1,'com1 from user1',?)," +
                                        "(1,3,'com2 from user3',?),"+
                                        "(2,2,'com2 from user2',?),"+
                                        "(2,3,'com3 from user3',?)";
            try(PreparedStatement insert_comments_stm = connection.prepareStatement(insert_comments)){
                for(int i=1; i<=6; i++){
                    insert_comments_stm.setTimestamp(i, Timestamp.from(
                            new Timestamp(System.currentTimeMillis()).toInstant().plusSeconds(120 + i*30)));
                }
                insert_comments_stm.executeUpdate();
            }

            // Populate followers table
            String insert_follows = "INSERT INTO followers VALUES(2,1),(3,1),(1,2),(2,3)";
            try(PreparedStatement insert_follows_stm = connection.prepareStatement(insert_follows)){
                insert_follows_stm.executeUpdate();
            }

        } catch (SQLException e) {
            logger.error("Database initialization");
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void clean_database(){
        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            try(PreparedStatement clean_stm = connection.prepareStatement(
                    "TRUNCATE TABLE users,comments, posts, followers RESTART IDENTITY CASCADE")){
                clean_stm.executeUpdate();
                logger.info("Cleaned Database");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void get_followers_posts_in_reverse_chrono(){
        HashMap<String, ArrayList<String>> results = testViewsRepository.getFollowersPostsInReverseChrono(2);
        ArrayList<String> posts_from_first_follower = results.get("user1");
        ArrayList<String> posts_from_second_follower = results.get("user3");

        assertEquals(2, results.keySet().size());
        assertEquals(2, results.get("user1").size());
        assertEquals(1, results.get("user3").size());
        assertEquals("post2 from user1",posts_from_first_follower.get(0));
        assertEquals("post1 from user1",posts_from_first_follower.get(1));
        assertEquals("post1 from user3",posts_from_second_follower.get(0));

    }

    @Test
    void get_own_posts_with_last_n_comments_in_reverse_chrono(){
        HashMap<String,ArrayList<String>> results =
                testViewsRepository.getOwnPostsWithLastNCommentsInReverseChrono(1,2);

        ArrayList<String> post1_comments = results.get("post1 from user1");
        ArrayList<String> post2_comments = results.get("post2 from user1");

        assertEquals(2,results.keySet().size());
        assertEquals(2,post1_comments.size());
        assertEquals(2,post2_comments.size());

        assertEquals("com2 from user3", post1_comments.get(0));
        assertEquals("com1 from user2", post1_comments.get(1));
        assertEquals("com3 from user3", post2_comments.get(0));
        assertEquals("com2 from user2", post2_comments.get(1));
    }

    @Test
    void get_all_comments_on_own_posts(){
        HashMap<String,ArrayList<String>> results = testViewsRepository.getAllCommentsOnOwnPosts(1);
        assertEquals(5,results.size());
    }

    @Test
    void get_latest_comments_on_own_or_followers_posts(){
        HashMap<String, String> results = testViewsRepository.getLatestCommentsOnOwnOrFollowersPosts(1);
        assertEquals(3,results.keySet().size());
        assertEquals("com2 from user3",results.get("post1 from user1"));
        assertEquals("com3 from user3",results.get("post2 from user1"));
        assertEquals("com1 from user1", results.get("post1 from user2"));
    }

    @Test
    void get_followers_of(){
        ArrayList<String> results = testViewsRepository.getFollowersOf(2);
        assertEquals(2,results.size());
    }

    @Test
    void get_users_to_follow_available(){
        ArrayList<String> results = testViewsRepository.getUsersToFollow(1);
        assertEquals(1,results.size());
    }
    @Test
    void get_users_to_follow_no_available(){
        ArrayList<String> results = testViewsRepository.getUsersToFollow(2);
        assertEquals(0,results.size());
    }
}
