package com.eventregistration.dao;
import java.sql.*;
public final class DatabaseInitializer {
    private DatabaseInitializer() {}
    public static void initialize() {
        try (Connection c = DatabaseConnection.getConnection(); Statement s = c.createStatement()) {
            // Keep legacy columns compatible with the uploaded database.
            s.executeUpdate(""" 
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL CHECK (roles IN ('TEACHER','STUDENT')),
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
            System.out.println("Database initialized successfully. Foreign keys enabled.");
        } catch (SQLException e) { throw new IllegalStateException("Database initialization failed", e); }
    }
}
