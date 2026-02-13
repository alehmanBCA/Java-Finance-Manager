package com.example.dao;

import com.example.db.Db;
import com.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    // Create user
    public int create(User user) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, email) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getEmail());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Insert succeeded but no id was returned.");
                int id = rs.getInt("id");
                user.setId(id);
                return id;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT id, first_name, last_name, username, password, email FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error logging in", e);
        }
    }

    // Read
    public List<User> findAll() {
        String sql = "SELECT id, first_name, last_name, username, password, email FROM users ORDER BY id";
        List<User> results = new ArrayList<>();

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(mapRow(rs));
            }
            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Error reading users", e);
        }
    }

    // Read (by id)
    public Optional<User> findById(int id) {
        String sql = "SELECT id, first_name, last_name, username, password, email FROM users WHERE id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id", e);
        }
    }

    // Update
    public boolean updateEmail(int id, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newEmail);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    // Delete
    public boolean deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            return rows == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email")
        );
    }
}