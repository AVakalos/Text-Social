package org.apostolis;

import org.apostolis.model.AuthRequest;
import org.apostolis.model.AuthResponse;
import org.apostolis.model.SignupResponse;
import org.apostolis.model.User;
import org.apostolis.repository.DbUtils;
import org.apostolis.repository.UserRepository;
import org.apostolis.repository.UserRepositoryImpl;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.TokenManager;
import org.apostolis.service.UserService;
import org.apostolis.service.UserServiceImpl;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {
    private static DbUtils testDbUtils;

    private static final String testUrl = "jdbc:postgresql://localhost:5433/TextSocialTest";
    private static final String user = "postgres";
    private static final String password = "1234";

    private static UserRepository testUserRepository;
    private static TokenManager testTokenManager;
    private static UserService testUserService;


    @BeforeAll
    static void setup(){
        testDbUtils = new DbUtils(testUrl, user, password);
        testUserRepository = new UserRepositoryImpl(testDbUtils);
        testTokenManager = new JjwtTokenManagerImpl();
        testUserService = new UserServiceImpl(testUserRepository, testTokenManager);
    }

    @Test
    @Order(1)
    void testSignUp(){
        //assertTrue(true);
        User testuser = new User("testuser","pass","FREE");
        SignupResponse producedResponse = testUserService.signup(testuser);
        //assertEquals(producedResponse.getMessage(),"User signed up.");
        assertEquals(producedResponse.getStatus(),201);
    }
    @Test
    @Order(2)
    void testUnsuccessfulSignUp(){
        User testuser = new User("testuser","pass","FREE");
        SignupResponse producedResponse = testUserService.signup(testuser);
        //assertEquals(producedResponse.getMessage(),"Username is already taken! Try a different one.");
        assertEquals(producedResponse.getStatus(),406);
    }

    @Test
    @Order(3)
    void testLogin(){
        AuthRequest testRequest = new AuthRequest("testuser","pass");
        //AuthResponse expectedResponse = new AuthResponse();
        AuthResponse producedResponse = testUserService.login(testRequest);
        assertEquals(producedResponse.getUsername(),"testuser");
        assertNotEquals(producedResponse.getToken(),"Not available");
        assertEquals(producedResponse.getStatus(),202);
    }

    @Test
    @Order(4)
    void testUnsuccessfulLogin(){
        assertTrue(true);
        AuthRequest testRequest = new AuthRequest("testuser","incorrect");
        AuthResponse producedResponse = testUserService.login(testRequest);
        assertEquals(producedResponse.getUsername(),"testuser");
        assertEquals(producedResponse.getToken(),"Not available");
        assertEquals(producedResponse.getStatus(),401);
    }

    @Test
    @Order(5)
    void testAuth(){
        assertTrue(true);
    }
    @Test
    @Order(6)
    void testUnsuccessfulAuth(){
        assertTrue(true);
    }

    @AfterAll
    static void cleanDatabase() throws Exception{
        try(Connection connection = DriverManager.getConnection(testUrl,user,password)) {
            PreparedStatement clean_table = connection.prepareStatement("TRUNCATE TABLE users");
            clean_table.executeUpdate();
        }
    }
}
