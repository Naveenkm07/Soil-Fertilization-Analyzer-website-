package util;

import model.User;
import model.SoilAnalysis;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/soil_analysis.db";
    private static Connection connection;

    static {
        try {
            // Create database directory if it doesn't exist
            java.io.File dbDir = new java.io.File("src/main/resources/database");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }
            
            // Initialize database connection
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        // Create users table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    full_name TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create soil_analysis table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS soil_analysis (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    type TEXT NOT NULL,
                    description TEXT,
                    status TEXT NOT NULL,
                    ph REAL,
                    nitrogen REAL,
                    phosphorus REAL,
                    potassium REAL,
                    organic_matter REAL,
                    moisture REAL,
                    location TEXT,
                    recommendations TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static User authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Note: In production, use proper password hashing
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(User user) {
        String query = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<SoilAnalysis> getRecentAnalyses(int userId) {
        List<SoilAnalysis> analyses = new ArrayList<>();
        String query = "SELECT * FROM soil_analysis WHERE user_id = ? ORDER BY date DESC LIMIT 10";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                analyses.add(new SoilAnalysis(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getDouble("ph"),
                    rs.getDouble("nitrogen"),
                    rs.getDouble("phosphorus"),
                    rs.getDouble("potassium"),
                    rs.getDouble("organic_matter"),
                    rs.getDouble("moisture"),
                    rs.getString("location"),
                    rs.getString("recommendations")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analyses;
    }

    public static boolean saveSoilAnalysis(SoilAnalysis analysis) {
        String query = "INSERT INTO soil_analysis (user_id, date, type, description, status, " +
                      "ph, nitrogen, phosphorus, potassium, organic_matter, moisture, location, recommendations) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, analysis.getUserId());
            pstmt.setString(2, analysis.getDate());
            pstmt.setString(3, analysis.getType());
            pstmt.setString(4, analysis.getDescription());
            pstmt.setString(5, analysis.getStatus());
            pstmt.setDouble(6, analysis.getPh());
            pstmt.setDouble(7, analysis.getNitrogen());
            pstmt.setDouble(8, analysis.getPhosphorus());
            pstmt.setDouble(9, analysis.getPotassium());
            pstmt.setDouble(10, analysis.getOrganicMatter());
            pstmt.setDouble(11, analysis.getMoisture());
            pstmt.setString(12, analysis.getLocation());
            pstmt.setString(13, analysis.getRecommendations());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getAnalysisCount(int userId) {
        String query = "SELECT COUNT(*) FROM soil_analysis WHERE user_id = ? AND date >= ?";
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, monthStart.toString());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getActiveRecommendationsCount(int userId) {
        String query = "SELECT COUNT(*) FROM soil_analysis WHERE user_id = ? AND status = 'Pending'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getAverageSoilHealth(int userId) {
        String query = "SELECT AVG((ph + nitrogen + phosphorus + potassium + organic_matter + moisture) / 6.0) " +
                      "FROM soil_analysis WHERE user_id = ? AND date >= ?";
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, monthStart.toString());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static List<SoilAnalysis> getAllAnalyses() throws SQLException {
        List<SoilAnalysis> analyses = new ArrayList<>();
        String query = "SELECT * FROM soil_analysis ORDER BY date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                SoilAnalysis analysis = new SoilAnalysis(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getDouble("ph"),
                    rs.getDouble("nitrogen"),
                    rs.getDouble("phosphorus"),
                    rs.getDouble("potassium"),
                    rs.getDouble("organic_matter"),
                    rs.getDouble("moisture"),
                    rs.getString("location"),
                    rs.getString("recommendations")
                );
                analyses.add(analysis);
            }
        }
        
        return analyses;
    }

    public static List<SoilAnalysis> searchAnalyses(int userId, String searchTerm) {
        List<SoilAnalysis> analyses = new ArrayList<>();
        String query = "SELECT * FROM soil_analysis WHERE user_id = ? AND " +
                      "(type LIKE ? OR description LIKE ? OR location LIKE ? OR status LIKE ?) " +
                      "ORDER BY date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setInt(1, userId);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                analyses.add(new SoilAnalysis(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("date"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getDouble("ph"),
                    rs.getDouble("nitrogen"),
                    rs.getDouble("phosphorus"),
                    rs.getDouble("potassium"),
                    rs.getDouble("organic_matter"),
                    rs.getDouble("moisture"),
                    rs.getString("location"),
                    rs.getString("recommendations")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analyses;
    }
} 