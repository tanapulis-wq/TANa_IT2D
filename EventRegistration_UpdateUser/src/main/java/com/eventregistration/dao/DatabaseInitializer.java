package com.eventregistration.dao;
import java.sql.*;
public final class DatabaseInitializer {
    private DatabaseInitializer() {}
    public static void initialize() {
        try (Connection c = DatabaseConnection.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL CHECK (roles IN ('TEACHER','STUDENT','ADMIN')),
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS venues (
                    venue_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,
                    address TEXT, city TEXT, capacity INTEGER
                )
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS events (
                    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    organizer_id INTEGER NOT NULL REFERENCES users(id),
                    venue_id INTEGER REFERENCES venues(venue_id), title TEXT NOT NULL,
                    description TEXT, starts_at TEXT, ends_at TEXT,
                    capacity INTEGER, status TEXT DEFAULT 'DRAFT'
                )
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS registrations (
                    registration_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event_id INTEGER NOT NULL REFERENCES events(event_id),
                    attendee_id INTEGER NOT NULL REFERENCES users(id),
                    ticket_code TEXT UNIQUE, quantity INTEGER DEFAULT 1,
                    status TEXT DEFAULT 'PENDING', registered_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    checked_in_at TEXT
                )
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS payments (
                    payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    registration_id INTEGER NOT NULL REFERENCES registrations(registration_id),
                    amount NUMERIC NOT NULL, method TEXT, transaction_ref TEXT,
                    status TEXT DEFAULT 'PENDING', paid_at TEXT
                )
                """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER REFERENCES users(id), action TEXT NOT NULL,
                    entity_type TEXT, entity_id INTEGER, ip_address TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
                """);
            allowAdminRole(c);
            System.out.println("Database initialized successfully. Foreign keys enabled.");
        } catch (SQLException e) { throw new IllegalStateException("Database initialization failed", e); }
    }

    /** Older databases only allow TEACHER/STUDENT in users.roles; rebuild the table once to also allow ADMIN. */
    private static void allowAdminRole(Connection c) throws SQLException {
        String ddl = null;
        try (PreparedStatement ps = c.prepareStatement("SELECT sql FROM sqlite_master WHERE type = ? AND name = ?")) {
            ps.setString(1, "table");
            ps.setString(2, "users");
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) ddl = rs.getString(1); }
        }
        if (ddl == null || ddl.contains("'ADMIN'")) return;
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = OFF");
            st.execute("BEGIN");
            try {
                st.execute("""
                    CREATE TABLE users_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        roles TEXT NOT NULL CHECK (roles IN ('TEACHER','STUDENT','ADMIN')),
                        status TEXT NOT NULL DEFAULT 'PENDING'
                    )
                    """);
                st.execute("INSERT INTO users_new(id,username,password,roles,status) SELECT id,username,password,roles,status FROM users");
                st.execute("DROP TABLE users");
                st.execute("ALTER TABLE users_new RENAME TO users");
                st.execute("COMMIT");
            } catch (SQLException e) {
                st.execute("ROLLBACK");
                throw e;
            } finally {
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
    }
}
