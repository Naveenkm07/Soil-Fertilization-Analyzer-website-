package model;

import java.time.LocalDate;

public class SoilAnalysis {
    private int id;
    private int userId;
    private String date;
    private String type;
    private String description;
    private String status;
    private double ph;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private double organicMatter;
    private double moisture;
    private String location;
    private String recommendations;

    public SoilAnalysis() {
        // Default constructor
    }

    public SoilAnalysis(int id, int userId, String date, String type, String description, String status,
                       double ph, double nitrogen, double phosphorus, double potassium,
                       double organicMatter, double moisture, String location, String recommendations) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.type = type;
        this.description = description;
        this.status = status;
        this.ph = ph;
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.organicMatter = organicMatter;
        this.moisture = moisture;
        this.location = location;
        this.recommendations = recommendations;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPh() { return ph; }
    public void setPh(double ph) { this.ph = ph; }

    public double getNitrogen() { return nitrogen; }
    public void setNitrogen(double nitrogen) { this.nitrogen = nitrogen; }

    public double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }

    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }

    public double getOrganicMatter() { return organicMatter; }
    public void setOrganicMatter(double organicMatter) { this.organicMatter = organicMatter; }

    public double getMoisture() { return moisture; }
    public void setMoisture(double moisture) { this.moisture = moisture; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
} 