package org.apostolis;


import io.javalin.Javalin;
import org.apostolis.controller.OperationsController;
import org.apostolis.controller.UserController;
import org.apostolis.controller.ViewsController;
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
        OperationsService operationsService= new OperationsServiceImpl(
                operationsRepository, tokenManager, 1000, 3000, 5);
        OperationsController operationsController = new OperationsController(operationsService);

        ViewsRepository viewsRepository = new ViewsRepositoryImpl(dbUtils);
        ViewsController viewsController = new ViewsController(viewsRepository);

        Javalin app = Javalin.create().start(7777);

        app.post("/signup",userController::signup);
        app.post("/signin",userController::login);

        app.before("/api/*", userController::authenticate);
        app.post("/api/newpost",operationsController::create_post);
        app.post("/api/newcomment",operationsController::create_comment);
        app.post("/api/follow",operationsController::follow);
        app.delete("/api/unfollow",operationsController::unfollow);
        app.get("/api/user/{id}/createurl/{post}",operationsController::create_url_for_post);


        app.get("api/user/{id}/followers/posts", viewsController::get_followers_posts_in_reverse_chrono);
        app.get("api/user/{id}/posts",viewsController::get_own_posts_with_last_100_comments_in_reverse_chrono);
        app.get("api/user/{id}/posts/comments",viewsController::get_all_comments_on_own_posts);
        app.get("api/user/{id}/latestcomments",viewsController::get_latest_comments_on_own_or_followers_posts);
        app.get("api/user/{id}/followers",viewsController::get_followers_of);
        app.get("api/user/{id}/tofollow",viewsController::get_users_to_follow);

        app.get("<url>",operationsController::decode_url);

    }
}
