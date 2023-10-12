package org.apostolis;

import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.repository.DbUtils;
import org.apostolis.repository.UserRepository;
import org.apostolis.repository.UserRepositoryImpl;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.security.TokenManager;
import org.apostolis.service.UserService;
import org.apostolis.service.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {
    private static DbUtils testDbUtils;

    private static final String testUrl = "jdbc:postgresql://localhost:5433/TextSocialTest";
    private static final String user = "postgres";
    private static final String password = "1234";

    private static UserRepository testUserRepository;
    private static TokenManager testTokenManager;
    private static PasswordEncoder testPasswordEncoder;
    private static UserService testUserService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);


    @BeforeAll
    static void setup(){
        testDbUtils = new DbUtils(testUrl, user, password);
        testUserRepository = new UserRepositoryImpl(testDbUtils);
        testTokenManager = new JjwtTokenManagerImpl();
        testPasswordEncoder = new PasswordEncoder();
        testUserService = new UserServiceImpl(testUserRepository, testTokenManager,testPasswordEncoder);
        logger.info("Initial setup of test");
    }

    @BeforeEach
    void setupDatabase(){
        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            String clean = "TRUNCATE TABLE users";
            String encoded_password = testPasswordEncoder.encodePassword("pass1");
            String insert = String.format("INSERT INTO users (username,password,role) VALUES('testuser1','%s','FREE')",encoded_password);
            PreparedStatement initialize_table = connection.prepareStatement(insert);
            PreparedStatement clean_table = connection.prepareStatement(clean);
            clean_table.executeUpdate();
            initialize_table.executeUpdate();
        }catch(SQLException e){
            logger.error("Setup database between test methods failed.",e.getMessage());
        }
    }

    @Test
    void testSignUp(){
        User testuser = new User("testuser","pass","FREE");
        SignupResponse producedResponse = testUserService.signup(testuser);
        assertEquals(producedResponse.getStatus(),201);
    }
    @Test
    void testUnsuccessfulSignUp(){
        User testuser = new User("testuser1","pass1","FREE");
        SignupResponse producedResponse = testUserService.signup(testuser);
        assertEquals(producedResponse.getStatus(),406);
    }

    @Test
    void testLogin(){
        AuthRequest testRequest = new AuthRequest("testuser1","pass1");
        AuthResponse producedResponse = testUserService.login(testRequest);
        assertEquals(producedResponse.getStatus(),202);
    }

    @Test
    void testUnsuccessfulLogin(){
        AuthRequest testRequest = new AuthRequest("testuser1","incorrect");
        AuthResponse producedResponse = testUserService.login(testRequest);
        assertEquals(producedResponse.getStatus(),401);
    }

    @Test
    void testAuth(){
        assertTrue(true);
    }
    @Test
    void testUnsuccessfulAuth(){
        assertTrue(true);
    }

    @AfterAll
    static void cleanDatabse(){
        String clean = "TRUNCATE TABLE users";
        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            PreparedStatement clean_table = connection.prepareStatement(clean);
            clean_table.executeUpdate();
            logger.info("Finally cleaned database");
        }catch(SQLException e){
            logger.error("Cleaning database after all test methods failed.",e.getMessage());
        }
    }
}
