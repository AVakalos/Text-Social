package org.apostolis.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apostolis.controller.OperationsController;
import org.apostolis.controller.UserController;
import org.apostolis.controller.ViewsController;
import org.apostolis.repository.*;
import org.apostolis.security.JjwtTokenManagerImpl;
import org.apostolis.security.PasswordEncoder;
import org.apostolis.security.TokenManager;
import org.apostolis.service.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Properties;

public class AppConfig {
    private final DbUtils dbUtils;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserController userController;

    private final OperationsRepository operationsRepository;
    private final OperationsService operationsService;
    private final RequestValidationService requestValidationService;
    private final OperationsController operationsController;

    private final ViewsRepository viewsRepository;

    private final ViewsService viewsService;
    private final ViewsController viewsController;

    private static HikariDataSource ds;

    public static Clock clock = Clock.system(ZoneId.of("Europe/Athens"));


    public AppConfig(String mode){
        dbUtils = new DbUtils();

        userRepository = new UserRepositoryImpl(dbUtils);
        tokenManager  = new JjwtTokenManagerImpl();
        passwordEncoder  = new PasswordEncoder();
        userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder);
        userController = new UserController(userService);

        operationsRepository = new OperationsRepositoryImpl(dbUtils);
        requestValidationService = new RequestValidationServiceImpl(tokenManager, userRepository, operationsRepository);


        if(mode.equals("production")){
            operationsService = new OperationsServiceImpl(
                    operationsRepository, tokenManager, requestValidationService,
                    1000, 3000, 5);
        } else if (mode.equals("test")) {
            operationsService = new OperationsServiceImpl(
                    operationsRepository, tokenManager, requestValidationService,
                    20,30,2);
        }else{
            throw new RuntimeException("Specify mode 'production' or 'test' when initializing AppConfig");
        }

        operationsController = new OperationsController(operationsService);

        viewsRepository = new ViewsRepositoryImpl(dbUtils);
        viewsService = new ViewsServiceImpl(viewsRepository, requestValidationService);
        viewsController = new ViewsController(viewsService);

        if(mode.equals("production")){
            Properties appProps = readProperties();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(appProps.getProperty("databaseUrl"));
            config.setUsername(appProps.getProperty("databaseUsername"));
            config.setPassword(appProps.getProperty("databasePassword"));
            ds = new HikariDataSource(config);

        } else if (mode.equals("test")) {
            Properties appProps = readProperties();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(appProps.getProperty("testDatabaseUrl"));
            config.setUsername(appProps.getProperty("testDatabaseUsername"));
            config.setPassword(appProps.getProperty("testDatabasePassword"));
            ds = new HikariDataSource(config);

        }else{
            throw new RuntimeException("Specify mode 'production' or 'test' when initializing AppConfig");
        }

    }

    public static Properties readProperties(){
        Properties appProps = new Properties();
        try {
            String propertiesPath = "target/classes/application.properties";
            appProps.load(Files.newInputStream(Paths.get(propertiesPath)));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return appProps;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }


    public DbUtils getDbUtils() {
        return dbUtils;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public UserService getUserService() {
        return userService;
    }

    public UserController getUserController() {
        return userController;
    }

    public OperationsRepository getOperationsRepository() {
        return operationsRepository;
    }

    public OperationsService getOperationsService() {
        return operationsService;
    }

    public RequestValidationService getRequestValidationService() {
        return requestValidationService;
    }

    public OperationsController getOperationsController() {
        return operationsController;
    }

    public ViewsRepository getViewsRepository() {
        return viewsRepository;
    }

    public ViewsController getViewsController() {
        return viewsController;
    }
}
