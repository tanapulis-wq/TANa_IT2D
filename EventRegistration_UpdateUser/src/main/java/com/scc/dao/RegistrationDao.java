package com.scc.dao;

import com.scc.model.Registration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDao {

    public boolean registerParticipant(Registration reg) {
        String sql = "INSERT INTO registrations(user_id, event_id, registration_date, registration_status) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reg.getUserId());
            pstmt.setInt(2, reg.getEventId());
            pstmt.setString(3, reg.getRegistrationDate());
            pstmt.setString(4, reg.getRegistrationStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Registration> getRegistrationsByUser(int userId) {
        List<Registration> regs = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Registration r = new Registration();
                r.setRegistrationId(rs.getInt("registration_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setEventId(rs.getInt("event_id"));
                r.setRegistrationDate(rs.getString("registration_date"));
                r.setRegistrationStatus(rs.getString("registration_status"));
                regs.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return regs;
    }

    public List<Registration> getRegistrationsByEvent(int eventId) {
        List<Registration> regs = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Registration r = new Registration();
                r.setRegistrationId(rs.getInt("registration_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setEventId(rs.getInt("event_id"));
                r.setRegistrationDate(rs.getString("registration_date"));
                r.setRegistrationStatus(rs.getString("registration_status"));
                regs.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return regs;
    }

    public boolean updateRegistrationStatus(int registrationId, String status) {
        String sql = "UPDATE registrations SET registration_status = ? WHERE registration_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, registrationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelRegistration(int registrationId) {
        return updateRegistrationStatus(registrationId, "CANCELED");
    }
}