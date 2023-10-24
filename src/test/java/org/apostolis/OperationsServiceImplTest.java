package org.apostolis;

import io.javalin.http.UnauthorizedResponse;
import org.apostolis.config.AppConfig;
import org.apostolis.model.*;
import org.apostolis.repository.*;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.security.TokenManager;
import org.apostolis.service.DbUtils;
import org.apostolis.service.OperationsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

// Integration Tests the operation service layer of the application

public class OperationsServiceImplTest {
    private static DbUtils testDbUtils;
    private static TokenManager testTokenManager;
    private static OperationsRepository testOperationsRepository;
    private static OperationsService testOperationService;
    private static PasswordEncoder testPasswordEncoder;

    static AppConfig testAppConfig = new AppConfig("test");


    static void preparePostAndFollow(Connection connection) throws SQLException{
        String insert_post = "INSERT INTO posts (user_id, text, created) VALUES (1,'first_post',?)";
        String insert_comments = "INSERT INTO comments (post_id, user_id, text, created) VALUES (1,2,'comment',?),(1,1,'mycomment',?)";
        String insert_follow = "INSERT INTO followers VALUES(1,2)";
        try(PreparedStatement add_post_stm = connection.prepareStatement(insert_post);
            PreparedStatement add_comments_stm = connection.prepareStatement(insert_comments);
            PreparedStatement add_follow_stm = connection.prepareStatement(insert_follow)){
            add_post_stm.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            add_post_stm.executeUpdate();
            add_comments_stm.setTimestamp(1, new Timestamp(System.currentTimeMillis()+2000));
            add_comments_stm.setTimestamp(2, new Timestamp(System.currentTimeMillis()+3000));
            add_comments_stm.executeUpdate();
            add_follow_stm.executeUpdate();
        }
    }

    // Prepare the database for the test cases
    @BeforeAll
    static void setup(){
        testDbUtils = testAppConfig.getDbUtils();
        testTokenManager = testAppConfig.getTokenManager();
        testPasswordEncoder = testAppConfig.getPasswordEncoder();

        DbUtils.ThrowingConsumer<Connection, Exception> setup_database = (connection) -> {
            try(PreparedStatement clean_stm = connection.prepareStatement(
                    "TRUNCATE TABLE users,comments, posts, followers RESTART IDENTITY CASCADE")){
                clean_stm.executeUpdate();
            }

            // register users
            String encoded_password = testPasswordEncoder.encodePassword("pass");
            String insert_user1 = "INSERT INTO users (username,password,role) VALUES('freeuser',?,'FREE')";
            String insert_user_2 = "INSERT INTO users (username,password,role) VALUES('premuser',?,'PREMIUM')";

            try(PreparedStatement insert_user_1_stm = connection.prepareStatement(insert_user1);
                PreparedStatement insert_user_2_stm = connection.prepareStatement(insert_user_2)){
                insert_user_1_stm.setString(1,encoded_password);
                insert_user_2_stm.setString(1, encoded_password);
                insert_user_1_stm.executeUpdate();
                insert_user_2_stm.executeUpdate();
            }
            // insert one post and a follow for the create post and unfollow test cases
            preparePostAndFollow(connection);
        };
        try {
            // initial clean for any possible garbage data
            testDbUtils.doInTransaction(setup_database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testOperationsRepository = testAppConfig.getOperationsRepository();
        testOperationService= testAppConfig.getOperationsService();
    }

    // Return the database to the initial state between test cases
    @BeforeEach
    void intermediateSetupDatabase() {
        DbUtils.ThrowingConsumer<Connection, Exception> intermediate_setup_database = (connection) -> {
            try(PreparedStatement clean_posts = connection.prepareStatement("TRUNCATE TABLE posts RESTART IDENTITY CASCADE");
                PreparedStatement clean_followers = connection.prepareStatement("TRUNCATE TABLE followers RESTART IDENTITY CASCADE");
                PreparedStatement clean_comments = connection.prepareStatement("TRUNCATE TABLE comments RESTART IDENTITY CASCADE")){
                clean_posts.executeUpdate();
                clean_comments.executeUpdate();
                clean_followers.executeUpdate();
            }
            preparePostAndFollow(connection);
        };
        try{
            testDbUtils.doInTransaction(intermediate_setup_database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void postFromFreeUserUnderLimit(){
        String token = testTokenManager.issueToken("freeuser",Role.FREE);
        Post post = new Post(1,"pass_length_post"); // 16
        testOperationService.createPost(post,token);
        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void postFromFreeUserExceedLimit(){
        String token = testTokenManager.issueToken("freeuser",Role.FREE);
        Post post = new Post(1,"pass_length_post_exceeding_the_limits"); // 37
        assertThrows(UnauthorizedResponse.class, () -> testOperationService.createPost(post,token));
    }

    @Test
    void postFromPremiumUserBetweenLimits(){
        String token = testTokenManager.issueToken("premuser",Role.PREMIUM);
        Post post = new Post(2,"post_length_greater_than_free"); // 29
        testOperationService.createPost(post,token);
        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void postFromPremiumUserExceedLimit(){
        String token = testTokenManager.issueToken("premuser",Role.PREMIUM);
        Post post = new Post(2,"pass_length_post_exceeding_the_limits"); // 37
        assertThrows(UnauthorizedResponse.class, () -> testOperationService.createPost(post,token));
    }

    @Test
    void commentFromFreeUser(){
        String token = testTokenManager.issueToken("freeuser",Role.FREE);
        Comment comment = new Comment(1,1,"first comment");
        testOperationService.createComment(comment, token);
        assertDoesNotThrow(() -> RuntimeException.class);
        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void commentFromFreeUserExceedLimit(){
        String token = testTokenManager.issueToken("freeuser",Role.FREE);
        Comment comment1 = new Comment(1,1,"comment1");
        Comment comment2 = new Comment(1,1,"comment2");
        Comment comment3 = new Comment(1,1,"comment3");

        assertThrows(UnauthorizedResponse.class, () -> {
            testOperationService.createComment(comment1, token);
            testOperationService.createComment(comment2, token);
            testOperationService.createComment(comment3, token);
        });
    }

    @Test
    void commentFromPremiumUser(){
        String token = testTokenManager.issueToken("premuser",Role.PREMIUM);
        Comment comment1 = new Comment(1,1,"comment1");
        Comment comment2 = new Comment(1,1,"comment2");
        Comment comment3 = new Comment(1,1,"comment3");
        testOperationService.createComment(comment1, token);
        testOperationService.createComment(comment2, token);
        testOperationService.createComment(comment3, token);

        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void follow(){
        testOperationService.follow(2,1);
        assertDoesNotThrow(() -> RuntimeException.class);
    }

    @Test
    void followYourselfNotAllowed(){
        assertThrows(UnauthorizedResponse.class, () -> testOperationService.follow(1,1));
    }

    @Test
    void unfollow(){
        testOperationService.unfollow(1,2);
        assertDoesNotThrow(() -> RuntimeException.class);
    }

    @Test
    void UrlForPostAndComments(){
        String url = testOperationService.createUrlForPostAndComments(1,1);
        HashMap<String, ArrayList<String>> decoded = testOperationService.decodeUrl(url);
        assertEquals(2,decoded.get("first_post").size());
        assertEquals("mycomment",decoded.get("first_post").get(0));
    }
}
