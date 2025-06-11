package util;

import model.SoilAnalysis;
import model.User;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataManager {
    private static final Map<Integer, User> users = new ConcurrentHashMap<>();
    private static final Map<Integer, SoilAnalysis> analyses = new ConcurrentHashMap<>();
    private static int nextUserId = 1;
    private static int nextAnalysisId = 1;
    
    // Initialize with some sample data
    static {
        // Add sample user
        User admin = new User(nextUserId++, "admin", "admin123", "Admin User", "admin@example.com", "ADMIN");
        users.put(admin.getId(), admin);
        
        // Add sample analyses
        LocalDate today = LocalDate.now();
        addSampleAnalysis(admin.getId(), today.minusDays(30), "Field A", 6.5, 45.0, 35.0, 180.0, 3.2, 25.0);
        addSampleAnalysis(admin.getId(), today.minusDays(15), "Field B", 7.2, 50.0, 40.0, 200.0, 3.5, 28.0);
        addSampleAnalysis(admin.getId(), today, "Field A", 6.8, 48.0, 38.0, 190.0, 3.4, 26.0);
    }
    
    private static void addSampleAnalysis(int userId, LocalDate date, String location, 
                                        double ph, double nitrogen, double phosphorus, 
                                        double potassium, double organicMatter, double moisture) {
        SoilAnalysis analysis = new SoilAnalysis(
            nextAnalysisId++,
            userId,
            date.toString(),
            "Standard Analysis",
            "Regular soil analysis",
            "Completed",
            ph,
            nitrogen,
            phosphorus,
            potassium,
            organicMatter,
            moisture,
            location,
            generateRecommendations(ph, nitrogen, phosphorus, potassium, organicMatter, moisture)
        );
        analyses.put(analysis.getId(), analysis);
    }
    
    private static String generateRecommendations(double ph, double nitrogen, double phosphorus, 
                                               double potassium, double organicMatter, double moisture) {
        List<String> recommendations = new ArrayList<>();
        
        // pH recommendations
        if (ph < 6.0) {
            recommendations.add("Consider adding lime to raise soil pH");
        } else if (ph > 7.5) {
            recommendations.add("Consider adding sulfur to lower soil pH");
        }
        
        // Nutrient recommendations
        if (nitrogen < 40) {
            recommendations.add("Apply nitrogen-rich fertilizer");
        }
        if (phosphorus < 30) {
            recommendations.add("Add phosphate fertilizer");
        }
        if (potassium < 150) {
            recommendations.add("Apply potash fertilizer");
        }
        
        // Organic matter recommendations
        if (organicMatter < 3.0) {
            recommendations.add("Add organic compost to improve soil structure");
        }
        
        // Moisture recommendations
        if (moisture < 20) {
            recommendations.add("Increase irrigation frequency");
        } else if (moisture > 35) {
            recommendations.add("Improve drainage to prevent waterlogging");
        }
        
        return recommendations.isEmpty() ? 
            "Soil conditions are optimal. Maintain current management practices." :
            String.join("\n• ", recommendations);
    }
    
    // User management methods
    public static User authenticateUser(String username, String password) {
        return users.values().stream()
            .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
            .findFirst()
            .orElse(null);
    }
    
    public static boolean registerUser(User user) {
        if (users.values().stream().anyMatch(u -> 
            u.getUsername().equals(user.getUsername()) || 
            u.getEmail().equals(user.getEmail()))) {
            return false;
        }
        
        user.setId(nextUserId++);
        users.put(user.getId(), user);
        return true;
    }
    
    // Analysis management methods
    public static List<SoilAnalysis> getRecentAnalyses(int userId) {
        return analyses.values().stream()
            .filter(a -> a.getUserId() == userId)
            .sorted((a1, a2) -> LocalDate.parse(a2.getDate())
                .compareTo(LocalDate.parse(a1.getDate())))
            .limit(10)
            .collect(Collectors.toList());
    }
    
    public static List<SoilAnalysis> getAllAnalyses() {
        return new ArrayList<>(analyses.values());
    }
    
    public static boolean saveSoilAnalysis(SoilAnalysis analysis) {
        if (analysis.getId() == 0) {
            analysis.setId(nextAnalysisId++);
        }
        analyses.put(analysis.getId(), analysis);
        return true;
    }
    
    public static int getAnalysisCount(int userId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        return (int) analyses.values().stream()
            .filter(a -> a.getUserId() == userId && 
                LocalDate.parse(a.getDate()).isAfter(monthStart.minusDays(1)))
            .count();
    }
    
    public static int getActiveRecommendationsCount(int userId) {
        return (int) analyses.values().stream()
            .filter(a -> a.getUserId() == userId && 
                a.getStatus().equals("Pending"))
            .count();
    }
    
    public static double getAverageSoilHealth(int userId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        return analyses.values().stream()
            .filter(a -> a.getUserId() == userId && 
                LocalDate.parse(a.getDate()).isAfter(monthStart.minusDays(1)))
            .mapToDouble(a -> (a.getPh() + a.getNitrogen() + a.getPhosphorus() + 
                             a.getPotassium() + a.getOrganicMatter() + a.getMoisture()) / 6.0)
            .average()
            .orElse(0.0);
    }
} 