package com.soilanalysis.model;

import java.time.LocalDateTime;

public class SoilData {
    private String id;
    private LocalDateTime analysisDate;
    private double ph;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private double organicMatter;
    private double moisture;
    private double temperature;
    private double bulkDensity;
    private double cationExchangeCapacity;
    private SoilType soilType;
    private String location;
    private String sampleDepth;
    private String cropType;
    private String season;
    
    // Micronutrients
    private double iron;
    private double zinc;
    private double copper;
    private double manganese;
    
    // Soil texture percentages
    private double sandPercentage;
    private double siltPercentage;
    private double clayPercentage;

    public enum SoilType {
        CLAY,
        SANDY,
        LOAMY,
        SILTY,
        PEATY,
        CHALKY
    }

    public enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }

    // Constructor
    public SoilData() {
        this.id = generateId();
        this.analysisDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }

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

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getBulkDensity() { return bulkDensity; }
    public void setBulkDensity(double bulkDensity) { this.bulkDensity = bulkDensity; }

    public double getCationExchangeCapacity() { return cationExchangeCapacity; }
    public void setCationExchangeCapacity(double cationExchangeCapacity) { 
        this.cationExchangeCapacity = cationExchangeCapacity; 
    }

    public SoilType getSoilType() { return soilType; }
    public void setSoilType(SoilType soilType) { this.soilType = soilType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSampleDepth() { return sampleDepth; }
    public void setSampleDepth(String sampleDepth) { this.sampleDepth = sampleDepth; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public double getIron() { return iron; }
    public void setIron(double iron) { this.iron = iron; }

    public double getZinc() { return zinc; }
    public void setZinc(double zinc) { this.zinc = zinc; }

    public double getCopper() { return copper; }
    public void setCopper(double copper) { this.copper = copper; }

    public double getManganese() { return manganese; }
    public void setManganese(double manganese) { this.manganese = manganese; }

    public double getSandPercentage() { return sandPercentage; }
    public void setSandPercentage(double sandPercentage) { this.sandPercentage = sandPercentage; }

    public double getSiltPercentage() { return siltPercentage; }
    public void setSiltPercentage(double siltPercentage) { this.siltPercentage = siltPercentage; }

    public double getClayPercentage() { return clayPercentage; }
    public void setClayPercentage(double clayPercentage) { this.clayPercentage = clayPercentage; }

    // Helper method to generate unique ID
    private String generateId() {
        return "SOIL-" + System.currentTimeMillis();
    }

    // Helper method to validate soil texture percentages
    public boolean validateTexturePercentages() {
        double total = sandPercentage + siltPercentage + clayPercentage;
        return Math.abs(total - 100.0) < 0.01; // Allow for small floating-point differences
    }

    // Helper method to determine soil texture class based on percentages
    public String determineSoilTextureClass() {
        if (!validateTexturePercentages()) {
            return "Invalid texture percentages";
        }

        // USDA Soil Texture Triangle classification
        if (clayPercentage >= 40) {
            return "Clay";
        } else if (sandPercentage >= 45 && clayPercentage < 27) {
            return "Sandy Loam";
        } else if (clayPercentage >= 27 && clayPercentage < 40) {
            return "Clay Loam";
        } else if (sandPercentage >= 45 && clayPercentage >= 27) {
            return "Sandy Clay Loam";
        } else if (siltPercentage >= 50) {
            return "Silt Loam";
        } else {
            return "Loam";
        }
    }

    @Override
    public String toString() {
        return "SoilData{" +
                "id='" + id + '\'' +
                ", analysisDate=" + analysisDate +
                ", ph=" + ph +
                ", nitrogen=" + nitrogen +
                ", phosphorus=" + phosphorus +
                ", potassium=" + potassium +
                ", organicMatter=" + organicMatter +
                ", moisture=" + moisture +
                ", temperature=" + temperature +
                ", bulkDensity=" + bulkDensity +
                ", cationExchangeCapacity=" + cationExchangeCapacity +
                ", soilType=" + soilType +
                ", location='" + location + '\'' +
                ", sampleDepth='" + sampleDepth + '\'' +
                ", cropType='" + cropType + '\'' +
                ", season='" + season + '\'' +
                ", iron=" + iron +
                ", zinc=" + zinc +
                ", copper=" + copper +
                ", manganese=" + manganese +
                ", sandPercentage=" + sandPercentage +
                ", siltPercentage=" + siltPercentage +
                ", clayPercentage=" + clayPercentage +
                '}';
    }
} 