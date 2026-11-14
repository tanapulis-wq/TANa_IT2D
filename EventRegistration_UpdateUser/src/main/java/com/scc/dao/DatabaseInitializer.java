package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Table 1: Users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "account_status TEXT DEFAULT 'ACTIVE')");

            // Table 2: Events
            stmt.execute("CREATE TABLE IF NOT EXISTS events (" +
                    "event_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "event_name TEXT NOT NULL, " +
                    "event_date TEXT NOT NULL, " +
                    "event_time TEXT NOT NULL, " +
                    "venue TEXT NOT NULL, " +
                    "description TEXT, " +
                    "capacity INTEGER, " +
                    "status TEXT DEFAULT 'ACTIVE')");

            // Table 3: Registrations
            stmt.execute("CREATE TABLE IF NOT EXISTS registrations (" +
                    "registration_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "event_id INTEGER, " +
                    "registration_date TEXT, " +
                    "registration_status TEXT DEFAULT 'REGISTERED', " +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
                    "FOREIGN KEY(event_id) REFERENCES events(event_id))");

            // Table 4: Activity_Logs
            stmt.execute("CREATE TABLE IF NOT EXISTS activity_logs (" +
                    "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "action TEXT, " +
                    "date_time TEXT, " +
                    "description TEXT)");

            // Seed default admin if missing
            UserDao userDao = new UserDao();
            if (userDao.getUserByEmail("admin@scc.com") == null) {
                com.scc.model.User defaultAdmin = new com.scc.model.User(
                        "System Administrator",
                        "admin@scc.com",
                        SecurityUtil.hashPassword("admin123"),
                        "ADMIN",
                        "ACTIVE"
                );
                userDao.registerUser(defaultAdmin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}