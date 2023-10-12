package org.apostolis;


import io.javalin.Javalin;
import org.apostolis.controller.UserController;
import org.apostolis.repository.DbUtils;
import org.apostolis.repository.UserRepository;
import org.apostolis.repository.UserRepositoryImpl;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.TokenManager;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.service.UserService;
import org.apostolis.service.UserServiceImpl;


public class App
{
    public static void main( String[] args ){

        // Instantiate the controllers
        DbUtils dbUtils = new DbUtils();
        UserRepository userRepository = new UserRepositoryImpl(dbUtils);
        TokenManager tokenManager = new JjwtTokenManagerImpl();
        PasswordEncoder passwordEncoder = new PasswordEncoder();
        UserService userService = new UserServiceImpl(userRepository, tokenManager,passwordEncoder);
        UserController userController = new UserController(userService);

        Javalin app = Javalin.create().start(7777);

        app.post("/signup",userController::signup);
        app.post("/signin",userController::login);

        app.before("/api/*", userController::authorize);


    }
}
