package service;

import db.DatabaseConnection;
import model.User;

import java.sql.*;

public class ATMService {

    public User authenticate(String username, String pin) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND pin = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("pin"),
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String pin, double initialDeposit) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if username already exists
            String checkQuery = "SELECT id FROM users WHERE username = ?";
         PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; 
            }

            String insertQuery = "INSERT INTO users (username, pin, balance) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, username);
            stmt.setString(2, pin);
            stmt.setDouble(3, initialDeposit);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deposit(User user, double amount) {
        user.setBalance(user.getBalance() + amount);
        return updateBalance(user);
    }

    public boolean withdraw(User user, double amount) {
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            return updateBalance(user);
        }
        return false;
    }

    private boolean updateBalance(User user) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET balance = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, user.getBalance());
            stmt.setInt(2, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
