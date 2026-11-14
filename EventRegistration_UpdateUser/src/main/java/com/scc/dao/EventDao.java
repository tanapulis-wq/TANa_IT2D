package com.scc.dao;

import com.scc.model.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDao {
    public boolean addEvent(Event event) {
        String sql = "INSERT INTO events(event_name, event_date, event_time, venue, description, capacity, status) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getEventName());
            pstmt.setString(2, event.getEventDate());
            pstmt.setString(3, event.getEventTime());
            pstmt.setString(4, event.getVenue());
            pstmt.setString(5, event.getDescription());
            pstmt.setInt(6, event.getCapacity());
            pstmt.setString(7, event.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event e = new Event();
                e.setEventId(rs.getInt("event_id"));
                e.setEventName(rs.getString("event_name"));
                e.setEventDate(rs.getString("event_date"));
                e.setEventTime(rs.getString("event_time"));
                e.setVenue(rs.getString("venue"));
                e.setDescription(rs.getString("description"));
                e.setCapacity(rs.getInt("capacity"));
                e.setStatus(rs.getString("status"));
                events.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET event_name=?, event_date=?, event_time=?, venue=?, description=?, capacity=? WHERE event_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getEventName());
            pstmt.setString(2, event.getEventDate());
            pstmt.setString(3, event.getEventTime());
            pstmt.setString(4, event.getVenue());
            pstmt.setString(5, event.getDescription());
            pstmt.setInt(6, event.getCapacity());
            pstmt.setInt(7, event.getEventId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(int eventId) {
        String sql = "UPDATE events SET status = 'CANCELED' WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}