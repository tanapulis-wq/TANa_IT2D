package com.eventregistration.dao;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens connections to the SQLite database file (event_registration.db).
 * Foreign-key enforcement is turned on for EVERY connection, because SQLite
 * resets this setting each time a new connection is opened.
 */
public final class DatabaseConnection {

    private static final String DB_FILE = "event_registration.db";
    // Absolute path so it is obvious which file is used (it lives in the folder the program runs from).
    private static final Path DB_PATH = Path.of(DB_FILE).toAbsolutePath();
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private static boolean pathAnnounced = false;

    private DatabaseConnection() {}

    /** Full path of the database file in use, handy for checking you are looking at the right file. */
    public static String getDatabasePath() {
        return DB_PATH.toString();
    }

    public static Connection getConnection() throws SQLException {
        announcePathOnce();
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    private static synchronized void announcePathOnce() {
        if (!pathAnnounced) {
            pathAnnounced = true;
            System.out.println("Using database: " + DB_PATH);
        }
    }
}
