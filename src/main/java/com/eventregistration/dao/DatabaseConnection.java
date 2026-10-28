package com.eventregistration.dao;
import java.sql.*;
public final class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:event_registration.db";
    private DatabaseConnection() {}
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) { connection.close(); throw e; }
        return connection;
    }
}
