package org.apostolis;

import org.apostolis.config.AppConfig;
import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.service.DbUtils;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.security.TokenManager;
import org.apostolis.service.UserService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apostolis.model.Role;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/* Integration tests for the User Service layer of the application */

public class UserServiceImplTest {
    private static TokenManager testTokenManager;
    private static PasswordEncoder testPasswordEncoder;
    private static UserService testUserService;
    private static DbUtils testDbUtils;

    static AppConfig testAppConfig = new AppConfig("test");

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);


    @BeforeAll
    static void setup(){
        testDbUtils = testAppConfig.getDbUtils();
        testTokenManager = testAppConfig.getTokenManager();
        testPasswordEncoder = testAppConfig.getPasswordEncoder();
        testUserService = testAppConfig.getUserService();
        logger.info("Initial setup of test");
    }

    @AfterAll
    static void cleanDatabase(){

        DbUtils.ThrowingConsumer<Connection, Exception> clean_database = (connection) -> {
            String clean = "TRUNCATE TABLE users RESTART IDENTITY CASCADE";
            try(PreparedStatement clean_table = connection.prepareStatement(clean)){
                clean_table.executeUpdate();
                logger.info("Finally cleaned database");
            }
        };
        try {
            testDbUtils.doInTransaction(clean_database);
        }catch(Exception e){
            logger.error("Cleaning database after all test methods failed.");
            throw new RuntimeException(e.getMessage());
        }
    }

    @BeforeEach
    void setupDatabase(){
        DbUtils.ThrowingConsumer<Connection, Exception> setup_database = (connection) -> {
            String clean = "TRUNCATE TABLE users RESTART IDENTITY CASCADE";
            String encoded_password = testPasswordEncoder.encodePassword("pass1");
            String insert = "INSERT INTO users (username,password,role) VALUES('testuser1',?,'FREE')";
            try(PreparedStatement initialize_table = connection.prepareStatement(insert);
                PreparedStatement clean_table = connection.prepareStatement(clean)){
                clean_table.executeUpdate();
                initialize_table.setString(1, encoded_password);
                initialize_table.executeUpdate();
            }
        };

        try {
            testDbUtils.doInTransaction(setup_database);
        }catch(Exception e){
            logger.error("Setup database between test methods failed.");
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void testSignUp(){
        User testuser = new User("testuser","pass","FREE");
        SignupResponse producedResponse = testUserService.signup(testuser);
        assertEquals(201,producedResponse.getStatus());
    }
    @Test
    void testUnsuccessfulSignUp(){
        User testuser = new User("testuser1","pass1","FREE");
        SignupResponse producedResponse = testUserService.signup(testuser);
        assertEquals(406,producedResponse.getStatus());
    }

    @Test
    void testLogin(){
        AuthRequest testRequest = new AuthRequest("testuser1","pass1");
        AuthResponse producedResponse = testUserService.login(testRequest);
        assertEquals(202,producedResponse.getStatus());
    }

    @Test
    void testUnsuccessfulLogin(){
        AuthRequest testRequest = new AuthRequest("testuser1","incorrect");
        AuthResponse producedResponse = testUserService.login(testRequest);
        assertEquals(401,producedResponse.getStatus());
    }

    @Test
    void testAuth(){
        String token = testTokenManager.issueToken("testuser1",Role.valueOf("FREE"));
        assertTrue(testTokenManager.validateToken(token));
    }
    @Test
    void testUnsuccessfulAuth(){
        String token = testTokenManager.issueToken("testuser1",Role.valueOf("FREE"));
        String InvalidToken = token+"sdd";
        assertThrows(Exception.class, () -> testTokenManager.validateToken(InvalidToken));
    }

}
