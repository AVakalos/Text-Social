package org.apostolis;


import io.javalin.Javalin;
import org.apostolis.config.AppConfig;
import org.apostolis.controller.OperationsController;
import org.apostolis.controller.UserController;
import org.apostolis.controller.ViewsController;


public class App
{
    public static void main( String[] args ){

        AppConfig appConfig = new AppConfig("production");
        UserController userController = appConfig.getUserController();
        OperationsController operationsController = appConfig.getOperationsController();
        ViewsController viewsController = appConfig.getViewsController();

        int port = Integer.parseInt(AppConfig.readProperties().getProperty("port"));

        Javalin app = Javalin.create().start(port);

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
