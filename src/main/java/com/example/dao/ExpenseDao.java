package com.example.dao;

import com.example.db.Db;
import com.example.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDao {

    public int create(Expense expense) {
        String sql = """
                INSERT INTO expenses (user_id, description, amount) VALUES (?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, expense.getUserId());
            ps.setString(2, expense.getDescription());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(expense.getAmount()));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Insert succeeded but no id was returned.");
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating expense", e);
        }
    }

    public List<Expense> findByUserId(int userId) {
        String sql = """
                SELECT user_id, description, amount
                FROM expenses
                WHERE user_id = ?
                ORDER BY id
                """;

        List<Expense> results = new ArrayList<>();

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }

            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Error reading expenses", e);
        }
    }

    public double totalByUserId(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses WHERE user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal("total").doubleValue();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error totaling expenses", e);
        }
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getInt("user_id"),
                rs.getString("description"),
                rs.getBigDecimal("amount").doubleValue()
        );
    }
}
