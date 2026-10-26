package com.smartloan.dao;

import com.smartloan.model.User;
import com.smartloan.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public void register(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not register the account: " + e.getMessage(), e);
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT id, username, password, income, expenses, emergency_reserve " +
            "FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? readUser(result) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not authenticate the user.", e);
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT id, username, password, income, expenses, emergency_reserve " +
            "FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? readUser(result) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load user " + userId + ".", e);
        }
    }

    public boolean updateProfile(int userId, double income, double expenses, double emergencyReserve) {
        String sql = "UPDATE users SET income = ?, expenses = ?, emergency_reserve = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setDouble(1, income);
            statement.setDouble(2, expenses);
            statement.setDouble(3, emergencyReserve);
            statement.setInt(4, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Could not update the user profile.", e);
        }
    }

    private User readUser(ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getInt("id"));
        user.setUsername(result.getString("username"));
        user.setPassword(result.getString("password"));
        user.setIncome(result.getDouble("income"));
        user.setExpenses(result.getDouble("expenses"));
        user.setEmergencyReserve(result.getDouble("emergency_reserve"));
        return user;
    }
}
