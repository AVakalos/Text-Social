package org.apostolis.repository;


import org.apostolis.model.User;
import org.apostolis.security.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final DbUtils dbUtils;

    public UserRepositoryImpl(DbUtils dbUtils){
        this.dbUtils = dbUtils;
    }

    @Override
    public void save(User UserToSave, PasswordEncoder passwordEncoder) {
        DbUtils.ThrowingConsumer<Connection,Exception> insertUserIntoDb = (conn) -> {
            try(PreparedStatement insert_stm = conn.prepareStatement(
                    "INSERT INTO Users (username,password,role) VALUES(?,?,?)")){
                insert_stm.setString(1, UserToSave.getUsername());
                insert_stm.setString(2, passwordEncoder.encodePassword(UserToSave.getPassword()));
                insert_stm.setString(3,String.valueOf(UserToSave.getRole()));
                insert_stm.executeUpdate();
            }
        };
        try{
            dbUtils.doInTransaction(insertUserIntoDb);
            logger.info("User saved successfully in the database.");
        }catch (Exception e){
            logger.error("User didn't saved.");
        }
    }

    @Override
    public User getByUsername(String username) throws Exception {

        DbUtils.ThrowingFunction<Connection, User, Exception> retrieveUser = (conn) -> {
            try(PreparedStatement retrieve_stm = conn.prepareStatement(
                    "SELECT username,password,role FROM Users WHERE username=?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)){

                retrieve_stm.setString(1,username);
                ResultSet rs = retrieve_stm.executeQuery();
                User user = null;
                if(rs.next()){
                    logger.info("User: "+username+" found in the database");
                    rs.beforeFirst();
                    while(rs.next()){
                        user = new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                        );
                    }
                }else{
                    //throw new UserNotFoundException("User: "+username+" is not in the database");
                    return null;
                }
                return user;
            }
        };
        return dbUtils.doInTransaction(retrieveUser);
    }
}
