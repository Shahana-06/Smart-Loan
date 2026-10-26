package com.smartloan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseUtil {
    private static final String URL;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            String configuredPath = System.getenv("SMARTLOAN_DB");
            Path databasePath = Paths.get(
                configuredPath == null || configuredPath.trim().isEmpty()
                    ? "smartloan.db"
                    : configuredPath
            ).toAbsolutePath();
            Path parent = databasePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            URL = "jdbc:sqlite:" + databasePath;
            initializeDatabase();
        } catch (ClassNotFoundException | IOException | SQLException e) {
            throw new ExceptionInInitializerError(
                new IllegalStateException("Could not initialize the SmartLoan database.", e)
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    private static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "income REAL DEFAULT 0, " +
                "expenses REAL DEFAULT 0, " +
                "emergency_reserve REAL DEFAULT 0)");
            stmt.execute("CREATE TABLE IF NOT EXISTS loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "loan_type TEXT NOT NULL, " +
                "principal REAL NOT NULL, " +
                "interest_rate REAL NOT NULL, " +
                "tenure_months INTEGER NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id))");
            stmt.execute("INSERT OR IGNORE INTO users (id, username, password) VALUES (1, 'admin', 'admin')");
        }
    }
}
