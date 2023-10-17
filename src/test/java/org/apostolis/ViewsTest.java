package org.apostolis;

import org.apostolis.repository.*;
import org.apostolis.security.TokenManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ViewsTest {
    private static DbUtils testDbUtils;
    private static TokenManager testTokenManager;

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
            PreparedStatement clean_stm = connection.prepareStatement(
                    "TRUNCATE TABLE users,comments, posts, followers RESTART IDENTITY CASCADE");
            clean_stm.executeUpdate();

            // Populate users table
            String insert_users = "INSERT INTO users (username,password,role) VALUES" +
                                        "('user1','pass','role')," +
                                        "('user2','pass','role')," +
                                        "('user3','pass','role')";

            PreparedStatement insert_users_stm = connection.prepareStatement(insert_users);
            insert_users_stm.executeUpdate();

            // Populate posts table
            String insert_posts = "INSERT INTO posts (user_id, text, created) VALUES " +
                                        "(1,'1post1',?)," +
                                        "(1,'1post2',?)," +
                                        "(2,'2post1',?)," +
                                        "(3,'3post1',?)";
            PreparedStatement insert_posts_stm = connection.prepareStatement(insert_posts);
            for(int i=1; i<=4; i++){
                insert_posts_stm.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
            }
            insert_posts_stm.executeUpdate();

            // Populate comments table
            String insert_comments = "INSERT INTO comments (post_id,user_id,text,created) VALUES" +
                                        "(1,2,'from user2',?)," +
                                        "(2,1,'from user1',?)," +
                                        "(3,1,'from user1',?)";
            PreparedStatement insert_comments_stm = connection.prepareStatement(insert_comments);
            for(int i=1; i<=3; i++){
                insert_comments_stm.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
            }
            insert_comments_stm.executeUpdate();

            // Populate followers table
            String insert_follows = "INSERT INTO followers VALUES(2,1),(3,1),(1,2),(2,3)";
            PreparedStatement insert_follows_stm = connection.prepareStatement(insert_follows);
            insert_follows_stm.executeUpdate();

        } catch (SQLException e) {
            logger.error("Database initialization");
            throw new RuntimeException(e);
        }
    }

    private void printA(HashMap<String, ArrayList<String>> results){
        for(String key: results.keySet()){
            System.out.print(key+": ");
            for(String s: results.get(key)){
                System.out.print(s+", ");
            }
            System.out.println("\n");
        }
    }
    @Test
    void get_followers_posts(){
        HashMap<String, ArrayList<String>> results = testViewsRepository.get_followers_posts(2);
        assertEquals(2, results.keySet().size());
        assertEquals(2, results.get("user1").size());
        assertEquals(1, results.get("user3").size());
    }

    @Test
    void get_own_posts_with_last_100_comments(){
        HashMap<String,ArrayList<String>> results = testViewsRepository.get_own_posts_with_last_n_comments(2,2);
        //printA(results);
        assertEquals(1,results.keySet().size());
        assertEquals(1,results.get("2post1").size());
        // Check if comments returned is > 2 or insert 2 comments and see take the 2 latest
    }

    @Test
    void get_all_comments_on_posts(){
        ArrayList<String> results = testViewsRepository.get_all_comments_on_posts(1);
        assertEquals(2,results.size());
    }

    @Test
    void get_latest_comments_on_own_or_followers_posts(){
        HashMap<String, String> results = testViewsRepository.get_latest_comments_on_own_or_followers_posts(2);
        assertEquals(3,results.keySet().size());
        // test if the comment is actually the latest (insert a comment to all followers and own posts and assert it)
    }

    @Test
    void get_followers_of(){
        ArrayList<String> results = testViewsRepository.get_followers_of(2);
        assertEquals(2,results.size());
    }

    @Test
    void get_users_to_follow_available(){
        ArrayList<String> results = testViewsRepository.get_users_to_follow(1);
        assertEquals(1,results.size());
    }
    @Test
    void get_users_to_follow_no_available(){
        ArrayList<String> results = testViewsRepository.get_users_to_follow(2);
        assertEquals(0,results.size());
    }

    @AfterAll
    static void clean_database(){
        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            PreparedStatement clean_stm = connection.prepareStatement(
                    "TRUNCATE TABLE users,comments, posts, followers RESTART IDENTITY CASCADE");
            clean_stm.executeUpdate();
            logger.info("Cleaned Database");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
