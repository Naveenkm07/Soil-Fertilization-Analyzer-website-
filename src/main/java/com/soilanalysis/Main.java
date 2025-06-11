package com.soilanalysis;

import com.soilanalysis.model.SoilData;
import com.soilanalysis.model.SoilAnalysis;
import com.soilanalysis.service.SoilAnalyzerService;
import com.soilanalysis.service.ReportGeneratorService;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        try {
            // Create sample soil data with enhanced parameters
            SoilData soilData = new SoilData();
            
            // Basic parameters - optimal conditions maintained
            soilData.setPh(6.2);  // Optimal pH
            soilData.setNitrogen(38.0);  // Optimal nitrogen
            soilData.setPhosphorus(28.0);  // Optimal phosphorus
            soilData.setPotassium(42.0);  // Optimal potassium
            soilData.setOrganicMatter(3.2);  // Optimal organic matter
            soilData.setMoisture(32.0);  // Optimal moisture
            soilData.setSoilType(SoilData.SoilType.SANDY);  // Soil type remains the same
            soilData.setLocation("Field B, South Section - Multi-Crop Analysis");
            soilData.setSampleDepth("0-6 inches");
            
            // New parameters - optimal conditions
            soilData.setTemperature(20.5);  // Optimal temperature
            soilData.setBulkDensity(1.32);  // Optimal bulk density
            soilData.setCationExchangeCapacity(14.0);  // Optimal CEC
            
            // Multiple crop types for analysis
            String[] cropTypes = {"Wheat", "Corn", "Soybeans", "Cotton", "Rice", "Barley", "Oats", "Canola"};
            soilData.setCropType(String.join(",", cropTypes));  // Set multiple crops
            
            // Micronutrients (ppm) - optimal levels
            soilData.setIron(4.2);  // Optimal iron
            soilData.setZinc(2.8);  // Optimal zinc
            soilData.setCopper(0.9);  // Optimal copper
            soilData.setManganese(3.5);  // Optimal manganese
            
            // Soil texture percentages - remains the same
            soilData.setSandPercentage(65.0);
            soilData.setSiltPercentage(20.0);
            soilData.setClayPercentage(15.0);

            // Validate soil texture
            if (!soilData.validateTexturePercentages()) {
                System.out.println("Warning: Soil texture percentages do not sum to 100%");
            }
            System.out.println("Soil Texture Class: " + soilData.determineSoilTextureClass());
            
            // Analyze each season separately
            SoilAnalysis[] seasonalAnalyses = new SoilAnalysis[4];
            String[] seasons = {"SPRING", "SUMMER", "AUTUMN", "WINTER"};
            Map<String, String> allSeasonalRecommendations = new HashMap<>();
            
            System.out.println("\nAnalyzing soil suitability for multiple crops across seasons...");
            System.out.println("Crops: " + soilData.getCropType());
            
            // Analyze each season
            for (int i = 0; i < seasons.length; i++) {
                soilData.setSeason(seasons[i]);
                System.out.println("\nAnalyzing " + seasons[i] + " season...");
                
                // Analyze soil for this season
                SoilAnalyzerService analyzerService = new SoilAnalyzerService();
                seasonalAnalyses[i] = analyzerService.analyzeSoil(soilData);
                
                // Collect seasonal recommendations
                allSeasonalRecommendations.putAll(seasonalAnalyses[i].getSeasonalRecommendations());
            }
            
            // Use the last analysis for the report (they should be similar)
            SoilAnalysis finalAnalysis = seasonalAnalyses[0];
            finalAnalysis.setSeasonalRecommendations(allSeasonalRecommendations);

            // Generate PDF report
            ReportGeneratorService reportService = new ReportGeneratorService();
            String outputPath = "soil_analysis_report.pdf";
            reportService.generateReport(finalAnalysis, outputPath);

            System.out.println("\nSoil analysis completed successfully!");
            System.out.println("Report generated: " + outputPath);
            System.out.println("\nAnalysis Summary:");
            System.out.println("Health Score: " + finalAnalysis.getHealthScore() + "/10");
            System.out.println("Overall Assessment: " + finalAnalysis.getOverallAssessment());
            System.out.println("\nNumber of Recommendations: " + finalAnalysis.getRecommendations().size());
            System.out.println("Areas for Improvement: " + finalAnalysis.getImprovementAreas().size());
            
            // Print crop suitability summary
            System.out.println("\nCrop Suitability Summary:");
            for (SoilAnalysis.CropSuitability crop : finalAnalysis.getCropSuitability()) {
                System.out.printf("%s: %.1f/10 - %s%n", 
                    crop.getCropName(),
                    crop.getSuitabilityScore(),
                    crop.getSuitabilityScore() >= 7.0 ? "Highly Suitable" :
                    crop.getSuitabilityScore() >= 5.0 ? "Moderately Suitable" : "Not Recommended");
            }
            
            // Print seasonal recommendations
            System.out.println("\nSeasonal Management Recommendations:");
            for (Map.Entry<String, String> entry : finalAnalysis.getSeasonalRecommendations().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("Error during soil analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 