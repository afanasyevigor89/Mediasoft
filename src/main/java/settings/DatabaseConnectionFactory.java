package settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionFactory {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres_db";
    private static final String DB_USER = "postgres_user";
    private static final String DB_PASSWORD = "postgres_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static Connection getConnectionWithTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        return conn;
    }
}