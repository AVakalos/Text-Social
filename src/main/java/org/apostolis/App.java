package org.apostolis;


import io.javalin.Javalin;
import org.apostolis.controller.OperationsController;
import org.apostolis.controller.UserController;
import org.apostolis.controller.ViewsController;
import org.apostolis.repository.*;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.TokenManager;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.service.*;


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
        OperationsService operationsService= new OperationsServiceImpl(
                operationsRepository, tokenManager, 1000, 3000, 5);

        RequestValidationService requestValidationService = new RequestValidationServiceImpl(tokenManager, userRepository, operationsRepository);
        OperationsController operationsController = new OperationsController(operationsService, requestValidationService);

        ViewsRepository viewsRepository = new ViewsRepositoryImpl(dbUtils);
        ViewsController viewsController = new ViewsController(viewsRepository, requestValidationService);

        Javalin app = Javalin.create().start(7777);

        app.post("/signup",userController::signup);
        app.post("/signin",userController::login);

        app.before("/api/*", userController::authenticate);
        app.post("/api/newpost",operationsController::createPost);
        app.post("/api/newcomment",operationsController::createComment);
        app.post("/api/follow",operationsController::follow);
        app.delete("/api/unfollow",operationsController::unfollow);
        app.get("/api/user/{id}/createurl/{post}",operationsController::createUrlForPostAndComments);


        app.get("api/user/{id}/followers/posts", viewsController::getFollowersPostsInReverseChrono);
        app.get("api/user/{id}/posts",viewsController::getOwnPostsWithLast100CommentsInReverseChrono);
        app.get("api/user/{id}/posts/comments",viewsController::getAllCommentsOnOwnPosts);
        app.get("api/user/{id}/latestcomments",viewsController::getLatestCommentsOnOwnOrFollowersPosts);
        app.get("api/user/{id}/followers",viewsController::getFollowersOf);
        app.get("api/user/{id}/tofollow",viewsController::getUsersToFollow);

        app.get("<url>",operationsController::decodeLink);

    }
}
