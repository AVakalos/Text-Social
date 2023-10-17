package org.apostolis;

import io.javalin.http.UnauthorizedResponse;
import org.apostolis.model.*;
import org.apostolis.repository.*;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.security.TokenManager;
import org.apostolis.service.OperationsService;
import org.apostolis.service.OperationsServiceImpl;
import org.apostolis.service.UserService;
import org.apostolis.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;


public class OperationsServiceImplTest {
    private static DbUtils testDbUtils;
    private static TokenManager testTokenManager;
    private static UserRepository testUserRepository;
    private static UserService testUserService;
    private static OperationsRepository testOperationsRepository;
    private static OperationsService testOperationService;
    private static PasswordEncoder testPasswordEncoder;

    private static final String testUrl = "jdbc:postgresql://localhost:5433/TextSocialTest";
    private static final String user = "postgres";
    private static final String password = "1234";

    private static final Logger logger = LoggerFactory.getLogger(OperationsServiceImplTest.class);


    //
    @BeforeAll
    static void setup(){
        testDbUtils = new DbUtils(testUrl, user, password);
        testUserRepository = new UserRepositoryImpl(testDbUtils);
        testTokenManager = new JjwtTokenManagerImpl();
        testPasswordEncoder = new PasswordEncoder();
        testUserService = new UserServiceImpl(testUserRepository, testTokenManager,testPasswordEncoder);

        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            // initial clean for any possible garbage data
            PreparedStatement clean_stm = connection.prepareStatement(
                    "TRUNCATE TABLE users,comments, posts, followers RESTART IDENTITY CASCADE");
            clean_stm.executeUpdate();

            // register users
            User freeUser = new User("freeuser","pass","FREE");
            User premiumUser = new User("premuser","pass","PREMIUM");
            testUserService.signup(freeUser);
            testUserService.signup(premiumUser);

            // insert one post to allow comments creation
            String insert_stm = "INSERT INTO posts (user_id, text, created) VALUES (1,'first_post',?)";
            PreparedStatement add_post = connection.prepareStatement(insert_stm);
            add_post.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            add_post.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database initialization");
            throw new RuntimeException(e);
        }
        testOperationsRepository = new OperationsRepositoryImpl(testDbUtils);
        testOperationService= new OperationsServiceImpl(
                testOperationsRepository, testTokenManager,20,30,2);
    }

    // Return the database to the initial state
    @BeforeEach
    void intermediate_setup_database() {
        try(Connection connection = DriverManager.getConnection(testUrl,user,password)){

            PreparedStatement clean_posts = connection.prepareStatement("TRUNCATE TABLE posts RESTART IDENTITY CASCADE");
            PreparedStatement clean_followers = connection.prepareStatement("TRUNCATE TABLE followers RESTART IDENTITY CASCADE");
            PreparedStatement clean_comments = connection.prepareStatement("TRUNCATE TABLE comments RESTART IDENTITY CASCADE");

            clean_posts.executeUpdate();
            clean_comments.executeUpdate();
            clean_followers.executeUpdate();

            String insert_stm = "INSERT INTO posts (user_id, text, created) VALUES (1,'first_post',?)";
            PreparedStatement add_post = connection.prepareStatement(insert_stm);
            add_post.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            add_post.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void post_from_free_user_under_limit(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        Post post = new Post(1,"pass_length_post"); // 16
        testOperationService.create_post(post,response.getToken());
        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void post_from_free_user_exceed_limit(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        Post post = new Post(1,"pass_length_post_exceeding_the_limits"); // 37
        assertThrows(UnauthorizedResponse.class, () -> testOperationService.create_post(post,response.getToken()));
    }

    @Test
    void post_from_premium_user_between_limits(){
        AuthResponse response = testUserService.login(new AuthRequest("premuser","pass"));
        Post post = new Post(2,"post_length_greater_than_free"); // 29
        testOperationService.create_post(post,response.getToken());
        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void post_from_premium_user_exceed_limit(){
        AuthResponse response = testUserService.login(new AuthRequest("premuser","pass"));
        Post post = new Post(2,"pass_length_post_exceeding_the_limits"); // 37
        assertThrows(UnauthorizedResponse.class, () -> testOperationService.create_post(post,response.getToken()));
    }

    @Test
    void comment_from_free_user(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        Comment comment = new Comment(1,1,"first comment");
        testOperationService.create_comment(comment, response.getToken());
        assertDoesNotThrow(() -> RuntimeException.class);
        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void comment_from_free_user_exceed_limit(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        Comment comment1 = new Comment(1,1,"comment1");
        Comment comment2 = new Comment(1,1,"comment2");
        Comment comment3 = new Comment(1,1,"comment3");

        assertThrows(UnauthorizedResponse.class, () -> {
            String token = response.getToken();
            testOperationService.create_comment(comment1, token);
            testOperationService.create_comment(comment2, token);
            testOperationService.create_comment(comment3, token);
        });
    }

    @Test
    void comment_from_premium_user(){
        AuthResponse response = testUserService.login(new AuthRequest("premuser","pass"));
        Comment comment1 = new Comment(1,1,"comment1");
        Comment comment2 = new Comment(1,1,"comment2");
        Comment comment3 = new Comment(1,1,"comment3");
        String token = response.getToken();
        testOperationService.create_comment(comment1, token);
        testOperationService.create_comment(comment2, token);
        testOperationService.create_comment(comment3, token);

        assertDoesNotThrow(() -> UnauthorizedResponse.class);
    }

    @Test
    void follow(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        testOperationService.follow(1,2);
        assertDoesNotThrow(() -> RuntimeException.class);
    }

    @Test
    void follow_yourself_not_allowed(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        assertThrows(UnauthorizedResponse.class, () -> testOperationService.follow(1,1));
    }

    @Test
    void unfollow(){
        AuthResponse response = testUserService.login(new AuthRequest("freeuser","pass"));
        testOperationService.unfollow(1,2);
        assertDoesNotThrow(() -> RuntimeException.class);
    }
}
