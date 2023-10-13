package org.apostolis;


import io.javalin.Javalin;
import org.apostolis.controller.OperationsController;
import org.apostolis.controller.UserController;
import org.apostolis.repository.*;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.TokenManager;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.service.OperationsService;
import org.apostolis.service.OperationsServiceImpl;
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

        OperationsRepository operationsRepository = new OperationsRepositoryImpl(dbUtils);
        OperationsService operationsService= new OperationsServiceImpl(operationsRepository, tokenManager);
        OperationsController operationsController = new OperationsController(operationsService);

        Javalin app = Javalin.create().start(7777);

        app.post("/signup",userController::signup);
        app.post("/signin",userController::login);

        app.before("/api/*", userController::authenticate);
        app.post("/api/newpost",operationsController::create_post);
        app.post("/api/newcomment",operationsController::create_comment);
        app.post("/api/follow",operationsController::follow);
        app.delete("/api/unfollow",operationsController::unfollow);


    }
}
