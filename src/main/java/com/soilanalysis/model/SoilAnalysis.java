package com.soilanalysis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SoilAnalysis {
    private String id;
    private SoilData soilData;
    private double healthScore;
    private List<FertilizerRecommendation> recommendations;
    private String overallAssessment;
    private List<String> improvementAreas;
    private Map<String, Double> nutrientScores;
    private Map<String, String> seasonalRecommendations;
    private List<CropSuitability> cropSuitability;
    private List<EnvironmentalImpact> environmentalImpacts;

    public static class FertilizerRecommendation {
        private String name;
        private String amount;
        private String frequency;
        private String benefits;
        private String notes;
        private Priority priority;
        private String applicationMethod;
        private String bestTimeToApply;
        private List<String> compatibleCrops;
        private List<String> incompatibleCrops;

        public enum Priority {
            HIGH,
            MEDIUM,
            LOW
        }

        // Constructor
        public FertilizerRecommendation() {
            this.compatibleCrops = new ArrayList<>();
            this.incompatibleCrops = new ArrayList<>();
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public String getBenefits() { return benefits; }
        public void setBenefits(String benefits) { this.benefits = benefits; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }

        public String getApplicationMethod() { return applicationMethod; }
        public void setApplicationMethod(String applicationMethod) { 
            this.applicationMethod = applicationMethod; 
        }

        public String getBestTimeToApply() { return bestTimeToApply; }
        public void setBestTimeToApply(String bestTimeToApply) { 
            this.bestTimeToApply = bestTimeToApply; 
        }

        public List<String> getCompatibleCrops() { return compatibleCrops; }
        public void setCompatibleCrops(List<String> compatibleCrops) { 
            this.compatibleCrops = compatibleCrops; 
        }

        public List<String> getIncompatibleCrops() { return incompatibleCrops; }
        public void setIncompatibleCrops(List<String> incompatibleCrops) { 
            this.incompatibleCrops = incompatibleCrops; 
        }
    }

    public static class CropSuitability {
        private String cropName;
        private double suitabilityScore;
        private List<String> advantages;
        private List<String> challenges;
        private String recommendedVariety;
        private String plantingSeason;

        // Constructor
        public CropSuitability() {
            this.advantages = new ArrayList<>();
            this.challenges = new ArrayList<>();
        }

        // Getters and Setters
        public String getCropName() { return cropName; }
        public void setCropName(String cropName) { this.cropName = cropName; }

        public double getSuitabilityScore() { return suitabilityScore; }
        public void setSuitabilityScore(double suitabilityScore) { 
            this.suitabilityScore = suitabilityScore; 
        }

        public List<String> getAdvantages() { return advantages; }
        public void setAdvantages(List<String> advantages) { 
            this.advantages = advantages; 
        }

        public List<String> getChallenges() { return challenges; }
        public void setChallenges(List<String> challenges) { 
            this.challenges = challenges; 
        }

        public String getRecommendedVariety() { return recommendedVariety; }
        public void setRecommendedVariety(String recommendedVariety) { 
            this.recommendedVariety = recommendedVariety; 
        }

        public String getPlantingSeason() { return plantingSeason; }
        public void setPlantingSeason(String plantingSeason) { 
            this.plantingSeason = plantingSeason; 
        }
    }

    public static class EnvironmentalImpact {
        private String impactType;
        private String description;
        private String severity;
        private List<String> mitigationStrategies;
        private String longTermEffect;

        // Constructor
        public EnvironmentalImpact() {
            this.mitigationStrategies = new ArrayList<>();
        }

        // Getters and Setters
        public String getImpactType() { return impactType; }
        public void setImpactType(String impactType) { this.impactType = impactType; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }

        public List<String> getMitigationStrategies() { return mitigationStrategies; }
        public void setMitigationStrategies(List<String> mitigationStrategies) { 
            this.mitigationStrategies = mitigationStrategies; 
        }

        public String getLongTermEffect() { return longTermEffect; }
        public void setLongTermEffect(String longTermEffect) { 
            this.longTermEffect = longTermEffect; 
        }
    }

    // Constructor
    public SoilAnalysis() {
        this.id = generateId();
        this.recommendations = new ArrayList<>();
        this.improvementAreas = new ArrayList<>();
        this.nutrientScores = new HashMap<>();
        this.seasonalRecommendations = new HashMap<>();
        this.cropSuitability = new ArrayList<>();
        this.environmentalImpacts = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public SoilData getSoilData() { return soilData; }
    public void setSoilData(SoilData soilData) { this.soilData = soilData; }

    public double getHealthScore() { return healthScore; }
    public void setHealthScore(double healthScore) { this.healthScore = healthScore; }

    public List<FertilizerRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<FertilizerRecommendation> recommendations) { 
        this.recommendations = recommendations; 
    }

    public String getOverallAssessment() { return overallAssessment; }
    public void setOverallAssessment(String overallAssessment) { 
        this.overallAssessment = overallAssessment; 
    }

    public List<String> getImprovementAreas() { return improvementAreas; }
    public void setImprovementAreas(List<String> improvementAreas) { 
        this.improvementAreas = improvementAreas; 
    }

    public Map<String, Double> getNutrientScores() { return nutrientScores; }
    public void setNutrientScores(Map<String, Double> nutrientScores) { 
        this.nutrientScores = nutrientScores; 
    }

    public Map<String, String> getSeasonalRecommendations() { return seasonalRecommendations; }
    public void setSeasonalRecommendations(Map<String, String> seasonalRecommendations) { 
        this.seasonalRecommendations = seasonalRecommendations; 
    }

    public List<CropSuitability> getCropSuitability() { return cropSuitability; }
    public void setCropSuitability(List<CropSuitability> cropSuitability) { 
        this.cropSuitability = cropSuitability; 
    }

    public List<EnvironmentalImpact> getEnvironmentalImpacts() { return environmentalImpacts; }
    public void setEnvironmentalImpacts(List<EnvironmentalImpact> environmentalImpacts) { 
        this.environmentalImpacts = environmentalImpacts; 
    }

    // Helper method to generate unique ID
    private String generateId() {
        return "ANALYSIS-" + System.currentTimeMillis();
    }

    // Helper method to add a recommendation
    public void addRecommendation(FertilizerRecommendation recommendation) {
        this.recommendations.add(recommendation);
    }

    // Helper method to add an improvement area
    public void addImprovementArea(String area) {
        this.improvementAreas.add(area);
    }

    public void addNutrientScore(String nutrient, double score) {
        this.nutrientScores.put(nutrient, score);
    }

    public void addSeasonalRecommendation(String season, String recommendation) {
        this.seasonalRecommendations.put(season, recommendation);
    }

    public void addCropSuitability(CropSuitability suitability) {
        this.cropSuitability.add(suitability);
    }

    public void addEnvironmentalImpact(EnvironmentalImpact impact) {
        this.environmentalImpacts.add(impact);
    }

    // Helper method to calculate overall health score
    public void calculateHealthScore() {
        double totalScore = 0.0;
        int factorCount = 0;

        // Basic nutrient scores
        if (soilData != null) {
            // pH score (optimal range: 6.0-7.0)
            double phScore = calculatePhScore(soilData.getPh());
            totalScore += phScore;
            factorCount++;

            // Macronutrient scores
            totalScore += calculateNutrientScore(soilData.getNitrogen(), 30.0, 50.0);
            totalScore += calculateNutrientScore(soilData.getPhosphorus(), 20.0, 40.0);
            totalScore += calculateNutrientScore(soilData.getPotassium(), 40.0, 60.0);
            factorCount += 3;

            // Organic matter score (optimal range: 2.0-4.0%)
            double omScore = calculateNutrientScore(soilData.getOrganicMatter(), 2.0, 4.0);
            totalScore += omScore;
            factorCount++;

            // Micronutrient scores
            totalScore += calculateNutrientScore(soilData.getIron(), 4.0, 6.0);
            totalScore += calculateNutrientScore(soilData.getZinc(), 2.0, 4.0);
            totalScore += calculateNutrientScore(soilData.getCopper(), 0.8, 1.2);
            totalScore += calculateNutrientScore(soilData.getManganese(), 3.0, 5.0);
            factorCount += 4;
        }

        this.healthScore = factorCount > 0 ? totalScore / factorCount : 0.0;
    }

    private double calculatePhScore(double ph) {
        if (ph >= 6.0 && ph <= 7.0) return 10.0;
        if (ph >= 5.5 && ph < 6.0) return 8.0;
        if (ph > 7.0 && ph <= 7.5) return 8.0;
        if (ph >= 5.0 && ph < 5.5) return 6.0;
        if (ph > 7.5 && ph <= 8.0) return 6.0;
        return 4.0;
    }

    private double calculateNutrientScore(double value, double minOptimal, double maxOptimal) {
        if (value >= minOptimal && value <= maxOptimal) return 10.0;
        if (value >= minOptimal * 0.8 && value < minOptimal) return 8.0;
        if (value > maxOptimal && value <= maxOptimal * 1.2) return 8.0;
        if (value >= minOptimal * 0.6 && value < minOptimal * 0.8) return 6.0;
        if (value > maxOptimal * 1.2 && value <= maxOptimal * 1.4) return 6.0;
        return 4.0;
    }

    @Override
    public String toString() {
        return "SoilAnalysis{" +
                "id='" + id + '\'' +
                ", soilData=" + soilData +
                ", healthScore=" + healthScore +
                ", recommendations=" + recommendations +
                ", overallAssessment='" + overallAssessment + '\'' +
                ", improvementAreas=" + improvementAreas +
                ", nutrientScores=" + nutrientScores +
                ", seasonalRecommendations=" + seasonalRecommendations +
                ", cropSuitability=" + cropSuitability +
                ", environmentalImpacts=" + environmentalImpacts +
                '}';
    }
} 