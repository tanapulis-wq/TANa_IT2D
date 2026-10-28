package com.eventregistration.dao;
import com.eventregistration.model.User;
import java.sql.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
public class UserDao {
    private static String hash(String password) {
        try {
            byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return "pbkdf2$120000$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(result);
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }
    private static boolean verify(String password, String stored) {
        if (stored == null || !stored.startsWith("pbkdf2$")) return false;
        try {
            String[] parts = stored.split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, Integer.parseInt(parts[1]), expected.length * 8);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) { return false; }
    }
    public boolean registerUser(String username, String password, String role) {
        if (!role.equals("TEACHER") && !role.equals("STUDENT")) return false;
        String sql = "INSERT INTO users(username,password,roles,status) VALUES(?,?,?,'PENDING')";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, hash(password)); ps.setString(3, role);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { System.out.println("Registration error: " + e.getMessage()); return false; }
    }
    public User login(String username, String password) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id,username,password,roles,status FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && verify(password, rs.getString("password")))
                    return new User(rs.getInt("id"), rs.getString("username"), "", rs.getString("roles"), rs.getString("status"));
            }
        } catch (SQLException e) { System.out.println("Login error: " + e.getMessage()); }
        return null;
    }
}
