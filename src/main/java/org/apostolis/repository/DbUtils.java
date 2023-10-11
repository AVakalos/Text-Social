package org.apostolis.repository;

import java.sql.*;
import static java.util.Objects.isNull;

public class DbUtils {
    private static final String url = "jdbc:postgresql://localhost:5433/TextSocial";
    private static final String user="postgres";
    private static final String password = "1234";

    private static final ThreadLocal<Connection> thlconn = new ThreadLocal<Connection>();

    // Custom Functional Interfaces to handle Exceptions in lambda expressions
    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    // Extend functional interfaces to throw Exceptions
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception>{
        R apply(T t) throws E;
    }

    // Overloaded function for transaction management
    // Function lambda
    public static <R> R doInTransaction(ThrowingFunction<Connection,R, Exception> dbtask) throws Exception{

        boolean is_parent_transaction = false;
        R rs = null;
        if (isNull(thlconn.get())){
            Connection conn = DriverManager.getConnection(url, user, password);
            thlconn.set(conn);
            is_parent_transaction = true;
        }
        Connection conn = thlconn.get();
        try{
            conn.setAutoCommit(false);
            rs = dbtask.apply(conn);
            conn.commit();

        }catch (SQLException e){
            System.out.println(e.getMessage());
            System.out.println("Rolling back the transaction.");
            conn.rollback();
            thlconn.remove();
            throw new RuntimeException(e);
        }
        if(is_parent_transaction){
            thlconn.remove();
        }
        return rs;
    }

    // Consumer lambda
    public static void doInTransaction(ThrowingConsumer<Connection, Exception> dbtask) throws Exception{

        boolean is_parent_transaction = false;
        if (isNull(thlconn.get())){
            Connection conn = DriverManager.getConnection(url, user, password);
            thlconn.set(conn);
            is_parent_transaction = true;
        }
        Connection conn = thlconn.get();
        try{
            conn.setAutoCommit(false);
            dbtask.accept(conn);
            conn.commit();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println("Rolling back the transaction.");
            conn.rollback();
            thlconn.remove();
            throw new RuntimeException(e);
        }
        if(is_parent_transaction){
            thlconn.remove();
        }
    }
}
