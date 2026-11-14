package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Updated database name to ERS.db
    private static final String URL = "jdbc:sqlite:ERS.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}