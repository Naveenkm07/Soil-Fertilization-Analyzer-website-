package com.soilanalysis.service;

import com.soilanalysis.model.SoilData;
import com.soilanalysis.model.SoilAnalysis;
import com.soilanalysis.model.SoilAnalysis.FertilizerRecommendation;
import com.soilanalysis.model.SoilAnalysis.CropSuitability;
import com.soilanalysis.model.SoilAnalysis.EnvironmentalImpact;
import com.soilanalysis.model.SoilData.SoilType;
import com.soilanalysis.model.SoilData.Season;

import java.util.Arrays;
import java.util.List;

public class SoilAnalyzerService {
    
    // Constants for optimal ranges
    private static final double OPTIMAL_PH_MIN = 6.0;
    private static final double OPTIMAL_PH_MAX = 7.0;
    private static final double OPTIMAL_NITROGEN_MIN = 40.0;
    private static final double OPTIMAL_NITROGEN_MAX = 80.0;
    private static final double OPTIMAL_PHOSPHORUS_MIN = 30.0;
    private static final double OPTIMAL_PHOSPHORUS_MAX = 60.0;
    private static final double OPTIMAL_POTASSIUM_MIN = 40.0;
    private static final double OPTIMAL_POTASSIUM_MAX = 80.0;
    private static final double OPTIMAL_ORGANIC_MATTER_MIN = 3.0;
    private static final double OPTIMAL_ORGANIC_MATTER_MAX = 6.0;
    private static final double OPTIMAL_MOISTURE_MIN = 25.0;
    private static final double OPTIMAL_MOISTURE_MAX = 35.0;

    // New constants for micronutrients
    private static final double OPTIMAL_IRON_MIN = 4.0;
    private static final double OPTIMAL_IRON_MAX = 6.0;
    private static final double OPTIMAL_ZINC_MIN = 2.0;
    private static final double OPTIMAL_ZINC_MAX = 4.0;
    private static final double OPTIMAL_COPPER_MIN = 0.8;
    private static final double OPTIMAL_COPPER_MAX = 1.2;
    private static final double OPTIMAL_MANGANESE_MIN = 3.0;
    private static final double OPTIMAL_MANGANESE_MAX = 5.0;

    // Temperature ranges for different crops
    private static final double OPTIMAL_TEMPERATURE_MIN = 15.0;
    private static final double OPTIMAL_TEMPERATURE_MAX = 25.0;

    public SoilAnalysis analyzeSoil(SoilData soilData) {
        SoilAnalysis analysis = new SoilAnalysis();
        analysis.setSoilData(soilData);
        
        // Calculate health score using the new method
        analysis.calculateHealthScore();
        
        // Generate recommendations
        generateRecommendations(analysis);
        
        // Generate overall assessment
        analysis.setOverallAssessment(generateOverallAssessment(analysis.getHealthScore()));
        
        // Identify improvement areas
        identifyImprovementAreas(analysis);
        
        // Analyze micronutrients
        analyzeMicronutrients(analysis);
        
        // Analyze crop suitability
        analyzeCropSuitability(analysis);
        
        // Analyze environmental impact
        analyzeEnvironmentalImpact(analysis);
        
        // Generate seasonal recommendations
        generateSeasonalRecommendations(analysis);
        
        return analysis;
    }

    private double calculateHealthScore(SoilData soilData) {
        double phScore = calculatePhScore(soilData.getPh());
        double nitrogenScore = calculateNutrientScore(soilData.getNitrogen(), OPTIMAL_NITROGEN_MIN, OPTIMAL_NITROGEN_MAX);
        double phosphorusScore = calculateNutrientScore(soilData.getPhosphorus(), OPTIMAL_PHOSPHORUS_MIN, OPTIMAL_PHOSPHORUS_MAX);
        double potassiumScore = calculateNutrientScore(soilData.getPotassium(), OPTIMAL_POTASSIUM_MIN, OPTIMAL_POTASSIUM_MAX);
        double organicMatterScore = calculateNutrientScore(soilData.getOrganicMatter(), OPTIMAL_ORGANIC_MATTER_MIN, OPTIMAL_ORGANIC_MATTER_MAX);
        double moistureScore = calculateNutrientScore(soilData.getMoisture(), OPTIMAL_MOISTURE_MIN, OPTIMAL_MOISTURE_MAX);

        // Weighted average of all scores
        return (phScore * 0.2 +
                nitrogenScore * 0.2 +
                phosphorusScore * 0.15 +
                potassiumScore * 0.15 +
                organicMatterScore * 0.2 +
                moistureScore * 0.1);
    }

    private double calculatePhScore(double ph) {
        if (ph >= OPTIMAL_PH_MIN && ph <= OPTIMAL_PH_MAX) {
            return 10.0;
        } else if (ph >= 5.5 && ph <= 7.5) {
            return 7.0;
        } else if (ph >= 5.0 && ph <= 8.0) {
            return 4.0;
        } else {
            return 1.0;
        }
    }

    private double calculateNutrientScore(double value, double min, double max) {
        if (value >= min && value <= max) {
            return 10.0;
        } else if (value >= min * 0.7 && value <= max * 1.3) {
            return 7.0;
        } else if (value >= min * 0.5 && value <= max * 1.5) {
            return 4.0;
        } else {
            return 1.0;
        }
    }

    private void generateRecommendations(SoilAnalysis analysis) {
        SoilData soilData = analysis.getSoilData();
        
        // pH recommendations
        if (soilData.getPh() < 5.5) {
            addRecommendation(analysis, "Agricultural Lime", 
                "50-100 lbs per 1000 sq ft", 
                "Once per season",
                "Raises soil pH to reduce acidity",
                "Apply evenly and water thoroughly",
                FertilizerRecommendation.Priority.HIGH);
        } else if (soilData.getPh() > 7.5) {
            addRecommendation(analysis, "Sulfur",
                "10-20 lbs per 1000 sq ft",
                "Once per season",
                "Lowers soil pH to reduce alkalinity",
                "May take several months to see full effect",
                FertilizerRecommendation.Priority.HIGH);
        }

        // Nitrogen recommendations
        if (soilData.getNitrogen() < OPTIMAL_NITROGEN_MIN) {
            addRecommendation(analysis, "Nitrogen-rich Fertilizer",
                "1-2 lbs per 1000 sq ft",
                "Every 4-6 weeks during growing season",
                "Promotes leaf growth and green color",
                "Water thoroughly after application to prevent burning",
                FertilizerRecommendation.Priority.HIGH);
        }

        // Phosphorus recommendations
        if (soilData.getPhosphorus() < OPTIMAL_PHOSPHORUS_MIN) {
            addRecommendation(analysis, "Phosphate Fertilizer",
                "2-3 lbs per 1000 sq ft",
                "Every 8-10 weeks",
                "Promotes root development and flowering",
                "Work into soil rather than surface application for best results",
                FertilizerRecommendation.Priority.MEDIUM);
        }

        // Potassium recommendations
        if (soilData.getPotassium() < OPTIMAL_POTASSIUM_MIN) {
            addRecommendation(analysis, "Potassium-rich Fertilizer",
                "1-2 lbs per 1000 sq ft",
                "Every 6-8 weeks",
                "Improves overall plant vigor and disease resistance",
                "Particularly important for fruit and root development",
                FertilizerRecommendation.Priority.MEDIUM);
        }

        // Organic matter recommendations
        if (soilData.getOrganicMatter() < OPTIMAL_ORGANIC_MATTER_MIN) {
            addRecommendation(analysis, "Compost",
                "1-2 inches layer",
                "Twice per year",
                "Improves soil structure, water retention, and nutrient availability",
                "Work into top 4-6 inches of soil if possible",
                FertilizerRecommendation.Priority.HIGH);
        }

        // Soil type specific recommendations
        switch(soilData.getSoilType()) {
            case CLAY:
                addRecommendation(analysis, "Gypsum",
                    "40 lbs per 1000 sq ft",
                    "Once per year",
                    "Improves clay soil structure and drainage",
                    "Best applied in fall or early spring",
                    FertilizerRecommendation.Priority.MEDIUM);
                break;
            case SANDY:
                addRecommendation(analysis, "Organic Mulch",
                    "3-4 inch layer",
                    "Twice per year",
                    "Improves water retention in sandy soils",
                    "Reapply as it breaks down",
                    FertilizerRecommendation.Priority.MEDIUM);
                break;
        }

        // Maintenance recommendation for healthy soil
        if (analysis.getHealthScore() > 7.0) {
            addRecommendation(analysis, "Balanced Fertilizer (10-10-10)",
                "1 lb per 1000 sq ft",
                "Every 8-10 weeks during growing season",
                "Maintains overall soil fertility",
                "Reduce frequency for native plants",
                FertilizerRecommendation.Priority.LOW);
        }
    }

    private void addRecommendation(SoilAnalysis analysis, String name, String amount,
                                 String frequency, String benefits, String notes,
                                 FertilizerRecommendation.Priority priority) {
        FertilizerRecommendation recommendation = new FertilizerRecommendation();
        recommendation.setName(name);
        recommendation.setAmount(amount);
        recommendation.setFrequency(frequency);
        recommendation.setBenefits(benefits);
        recommendation.setNotes(notes);
        recommendation.setPriority(priority);
        analysis.addRecommendation(recommendation);
    }

    private String generateOverallAssessment(double healthScore) {
        if (healthScore >= 8.0) {
            return "Excellent soil health. Your soil is in optimal condition for plant growth.";
        } else if (healthScore >= 6.0) {
            return "Good soil health. Some minor improvements can be made for optimal plant growth.";
        } else if (healthScore >= 4.0) {
            return "Fair soil health. Several improvements are needed for better plant growth.";
        } else {
            return "Poor soil health. Significant improvements are required for successful plant growth.";
        }
    }

    private void identifyImprovementAreas(SoilAnalysis analysis) {
        SoilData soilData = analysis.getSoilData();
        
        if (soilData.getPh() < OPTIMAL_PH_MIN || soilData.getPh() > OPTIMAL_PH_MAX) {
            analysis.addImprovementArea("Soil pH needs adjustment");
        }
        if (soilData.getNitrogen() < OPTIMAL_NITROGEN_MIN) {
            analysis.addImprovementArea("Nitrogen levels are low");
        }
        if (soilData.getPhosphorus() < OPTIMAL_PHOSPHORUS_MIN) {
            analysis.addImprovementArea("Phosphorus levels are low");
        }
        if (soilData.getPotassium() < OPTIMAL_POTASSIUM_MIN) {
            analysis.addImprovementArea("Potassium levels are low");
        }
        if (soilData.getOrganicMatter() < OPTIMAL_ORGANIC_MATTER_MIN) {
            analysis.addImprovementArea("Organic matter content needs improvement");
        }
        if (soilData.getMoisture() < OPTIMAL_MOISTURE_MIN) {
            analysis.addImprovementArea("Soil moisture levels are low");
        }
    }

    private void analyzeMicronutrients(SoilAnalysis analysis) {
        SoilData soilData = analysis.getSoilData();
        
        // Calculate and store nutrient scores
        analysis.addNutrientScore("Iron", calculateNutrientScore(soilData.getIron(), OPTIMAL_IRON_MIN, OPTIMAL_IRON_MAX));
        analysis.addNutrientScore("Zinc", calculateNutrientScore(soilData.getZinc(), OPTIMAL_ZINC_MIN, OPTIMAL_ZINC_MAX));
        analysis.addNutrientScore("Copper", calculateNutrientScore(soilData.getCopper(), OPTIMAL_COPPER_MIN, OPTIMAL_COPPER_MAX));
        analysis.addNutrientScore("Manganese", calculateNutrientScore(soilData.getManganese(), OPTIMAL_MANGANESE_MIN, OPTIMAL_MANGANESE_MAX));

        // Add recommendations for deficient micronutrients
        if (soilData.getIron() < OPTIMAL_IRON_MIN) {
            addRecommendation(analysis, "Iron Chelate",
                "1-2 oz per 1000 sq ft",
                "Every 4-6 weeks during growing season",
                "Corrects iron deficiency",
                "Apply as foliar spray for quick results",
                FertilizerRecommendation.Priority.MEDIUM);
        }

        if (soilData.getZinc() < OPTIMAL_ZINC_MIN) {
            addRecommendation(analysis, "Zinc Sulfate",
                "1-2 lbs per 1000 sq ft",
                "Once per year",
                "Corrects zinc deficiency",
                "Best applied in early spring",
                FertilizerRecommendation.Priority.MEDIUM);
        }
    }

    private void analyzeCropSuitability(SoilAnalysis analysis) {
        SoilData soilData = analysis.getSoilData();
        List<String> commonCrops = Arrays.asList("Corn", "Wheat", "Soybeans", "Cotton", "Rice");

        for (String crop : commonCrops) {
            CropSuitability suitability = new CropSuitability();
            suitability.setCropName(crop);
            
            // Calculate suitability score based on soil parameters
            double suitabilityScore = calculateCropSuitabilityScore(soilData, crop);
            suitability.setSuitabilityScore(suitabilityScore);
            
            // Add crop-specific advantages and challenges
            addCropSpecificDetails(suitability, soilData, crop);
            
            analysis.addCropSuitability(suitability);
        }
    }

    private double calculateCropSuitabilityScore(SoilData soilData, String crop) {
        double score = 0.0;
        int factors = 0;

        // pH suitability
        double phScore = calculatePhSuitabilityForCrop(soilData.getPh(), crop);
        score += phScore;
        factors++;

        // Nutrient suitability
        double nutrientScore = calculateNutrientSuitabilityForCrop(soilData, crop);
        score += nutrientScore;
        factors++;

        // Soil type suitability
        double soilTypeScore = calculateSoilTypeSuitability(soilData.getSoilType(), crop);
        score += soilTypeScore;
        factors++;

        return factors > 0 ? score / factors : 0.0;
    }

    private double calculatePhSuitabilityForCrop(double ph, String crop) {
        // Crop-specific pH ranges
        switch (crop.toLowerCase()) {
            case "corn":
                return (ph >= 5.8 && ph <= 7.0) ? 10.0 : (ph >= 5.5 && ph <= 7.5) ? 7.0 : 4.0;
            case "wheat":
                return (ph >= 6.0 && ph <= 7.5) ? 10.0 : (ph >= 5.5 && ph <= 8.0) ? 7.0 : 4.0;
            case "soybeans":
                return (ph >= 6.0 && ph <= 7.0) ? 10.0 : (ph >= 5.5 && ph <= 7.5) ? 7.0 : 4.0;
            default:
                return (ph >= 6.0 && ph <= 7.0) ? 10.0 : (ph >= 5.5 && ph <= 7.5) ? 7.0 : 4.0;
        }
    }

    private double calculateNutrientSuitabilityForCrop(SoilData soilData, String crop) {
        double score = 0.0;
        int factors = 0;

        // Basic nutrient requirements
        score += calculateNutrientScore(soilData.getNitrogen(), OPTIMAL_NITROGEN_MIN, OPTIMAL_NITROGEN_MAX);
        score += calculateNutrientScore(soilData.getPhosphorus(), OPTIMAL_PHOSPHORUS_MIN, OPTIMAL_PHOSPHORUS_MAX);
        score += calculateNutrientScore(soilData.getPotassium(), OPTIMAL_POTASSIUM_MIN, OPTIMAL_POTASSIUM_MAX);
        factors += 3;

        return factors > 0 ? score / factors : 0.0;
    }

    private double calculateSoilTypeSuitability(SoilType soilType, String crop) {
        switch (crop.toLowerCase()) {
            case "corn":
                return (soilType == SoilType.LOAMY || soilType == SoilType.CLAY) ? 10.0 : 7.0;
            case "wheat":
                return (soilType == SoilType.LOAMY || soilType == SoilType.SILTY) ? 10.0 : 7.0;
            case "soybeans":
                return (soilType == SoilType.LOAMY || soilType == SoilType.SILTY) ? 10.0 : 7.0;
            default:
                return (soilType == SoilType.LOAMY) ? 10.0 : 7.0;
        }
    }

    private void addCropSpecificDetails(CropSuitability suitability, SoilData soilData, String crop) {
        switch (crop.toLowerCase()) {
            case "corn":
                suitability.getAdvantages().add("Good nitrogen levels for corn growth");
                if (soilData.getPhosphorus() < OPTIMAL_PHOSPHORUS_MIN) {
                    suitability.getChallenges().add("Phosphorus levels may limit root development");
                }
                suitability.setRecommendedVariety("Select based on local climate");
                suitability.setPlantingSeason("Spring");
                break;
            case "wheat":
                suitability.getAdvantages().add("Suitable pH range for wheat");
                if (soilData.getPotassium() < OPTIMAL_POTASSIUM_MIN) {
                    suitability.getChallenges().add("Potassium levels may affect grain quality");
                }
                suitability.setRecommendedVariety("Winter wheat varieties");
                suitability.setPlantingSeason("Fall");
                break;
            // Add more crops as needed
        }
    }

    private void analyzeEnvironmentalImpact(SoilAnalysis analysis) {
        SoilData soilData = analysis.getSoilData();
        
        // Analyze potential environmental impacts
        if (soilData.getNitrogen() > OPTIMAL_NITROGEN_MAX) {
            EnvironmentalImpact impact = new EnvironmentalImpact();
            impact.setImpactType("Nutrient Runoff");
            impact.setDescription("High nitrogen levels may lead to water pollution");
            impact.setSeverity("Moderate");
            impact.getMitigationStrategies().add("Implement controlled-release fertilizers");
            impact.getMitigationStrategies().add("Use cover crops to reduce nutrient leaching");
            impact.setLongTermEffect("Reduced water quality if not managed");
            analysis.addEnvironmentalImpact(impact);
        }

        if (soilData.getOrganicMatter() < OPTIMAL_ORGANIC_MATTER_MIN) {
            EnvironmentalImpact impact = new EnvironmentalImpact();
            impact.setImpactType("Soil Erosion");
            impact.setDescription("Low organic matter may increase soil erosion risk");
            impact.setSeverity("High");
            impact.getMitigationStrategies().add("Add organic matter through composting");
            impact.getMitigationStrategies().add("Implement conservation tillage");
            impact.setLongTermEffect("Improved soil structure and reduced erosion");
            analysis.addEnvironmentalImpact(impact);
        }
    }

    private void generateSeasonalRecommendations(SoilAnalysis analysis) {
        SoilData soilData = analysis.getSoilData();
        
        // Generate recommendations for each season
        analysis.addSeasonalRecommendation("SPRING", 
            "Prepare soil for planting. Apply balanced fertilizer and test soil temperature.");
        analysis.addSeasonalRecommendation("SUMMER", 
            "Monitor soil moisture and apply mulch to conserve water.");
        analysis.addSeasonalRecommendation("AUTUMN", 
            "Add organic matter and prepare soil for winter.");
        analysis.addSeasonalRecommendation("WINTER", 
            "Protect soil from erosion and plan for spring planting.");

        // Add season-specific recommendations based on current conditions
        if (soilData.getSeason() != null) {
            switch (Season.valueOf(soilData.getSeason())) {
                case SPRING:
                    if (soilData.getTemperature() < OPTIMAL_TEMPERATURE_MIN) {
                        analysis.addSeasonalRecommendation("CURRENT_SEASON",
                            "Wait for soil to warm up before planting. Consider using row covers.");
                    }
                    break;
                case SUMMER:
                    if (soilData.getMoisture() < OPTIMAL_MOISTURE_MIN) {
                        analysis.addSeasonalRecommendation("CURRENT_SEASON",
                            "Implement irrigation schedule to maintain soil moisture.");
                    }
                    break;
                // Add more seasonal recommendations as needed
            }
        }
    }
} 