
import { SoilData, SoilType, FertilizerRecommendation, Analysis } from "../types/soil";

// Calculate soil health score based on parameters
export function calculateSoilHealthScore(soilData: SoilData): number {
  // Weight different parameters based on their importance
  const weights = {
    ph: 0.2,
    nitrogen: 0.2,
    phosphorus: 0.15,
    potassium: 0.15,
    organicMatter: 0.2,
    moisture: 0.1
  };
  
  // Normalize each parameter to a 0-10 scale
  const phScore = normalizePhScore(soilData.ph);
  const nitrogenScore = normalizeNutrientScore(soilData.nitrogen, 0, 100);
  const phosphorusScore = normalizeNutrientScore(soilData.phosphorus, 0, 100);
  const potassiumScore = normalizeNutrientScore(soilData.potassium, 0, 100);
  const organicMatterScore = normalizeNutrientScore(soilData.organicMatter, 0, 10);
  const moistureScore = normalizeMoistureScore(soilData.moisture);
  
  // Calculate weighted score
  const score = (
    phScore * weights.ph +
    nitrogenScore * weights.nitrogen +
    phosphorusScore * weights.phosphorus +
    potassiumScore * weights.potassium +
    organicMatterScore * weights.organicMatter +
    moistureScore * weights.moisture
  );
  
  // Return score rounded to 1 decimal place out of 10
  return Math.round(score * 10) / 10;
}

// Helper functions to normalize values to a 0-10 scale
function normalizePhScore(ph: number): number {
  // Optimal pH is around 6.5, score decreases as we move away
  const optimal = 6.5;
  const distance = Math.abs(ph - optimal);
  // pH between 6-7 is best (score 8-10)
  if (distance < 0.5) return 10 - distance * 2;
  // pH between 5.5-6 or 7-7.5 is good (score 6-8)
  if (distance < 1) return 8 - (distance - 0.5) * 4;
  // pH between 5-5.5 or 7.5-8 is acceptable (score 4-6)
  if (distance < 1.5) return 6 - (distance - 1) * 4;
  // pH between 4.5-5 or 8-8.5 is poor (score 2-4)
  if (distance < 2) return 4 - (distance - 1.5) * 4;
  // pH below 4.5 or above 8.5 is very poor (score 0-2)
  return Math.max(0, 2 - (distance - 2) * 2);
}

function normalizeNutrientScore(value: number, min: number, max: number): number {
  // Normalize to 0-10 scale
  const normalized = Math.min(10, Math.max(0, (value - min) / (max - min) * 10));
  // For nutrients, higher is generally better, but we want a bell curve
  // peaking at around 70-80% of the max value
  const optimal = max * 0.75;
  const ratio = value / optimal;
  if (ratio <= 1) return normalized; // Below optimal, linear score
  // Above optimal, gradually decrease score
  const excess = ratio - 1;
  return normalized * Math.max(0.5, 1 - excess);
}

function normalizeMoistureScore(moisture: number): number {
  // Optimal moisture is between 50-70%
  if (moisture >= 50 && moisture <= 70) return 10;
  // Too dry (below 50%)
  if (moisture < 50) return Math.max(0, moisture / 5);
  // Too wet (above 70%)
  return Math.max(0, 10 - (moisture - 70) / 3);
}

// Generate fertilizer recommendations based on soil data
export function generateRecommendations(soilData: SoilData): FertilizerRecommendation[] {
  const recommendations: FertilizerRecommendation[] = [];
  
  // Check pH levels
  if (soilData.ph < 5.5) {
    recommendations.push({
      name: "Agricultural Lime",
      amount: "50-100 lbs per 1000 sq ft",
      frequency: "Once per season",
      benefits: "Raises soil pH to reduce acidity",
      notes: "Apply evenly and water thoroughly"
    });
  } else if (soilData.ph > 7.5) {
    recommendations.push({
      name: "Sulfur",
      amount: "10-20 lbs per 1000 sq ft",
      frequency: "Once per season",
      benefits: "Lowers soil pH to reduce alkalinity",
      notes: "May take several months to see full effect"
    });
  }
  
  // Check nitrogen levels
  if (soilData.nitrogen < 50) {
    recommendations.push({
      name: "Nitrogen-rich Fertilizer",
      amount: "1-2 lbs per 1000 sq ft",
      frequency: "Every 4-6 weeks during growing season",
      benefits: "Promotes leaf growth and green color",
      notes: "Water thoroughly after application to prevent burning"
    });
  }
  
  // Check phosphorus levels
  if (soilData.phosphorus < 30) {
    recommendations.push({
      name: "Phosphate Fertilizer",
      amount: "2-3 lbs per 1000 sq ft",
      frequency: "Every 8-10 weeks",
      benefits: "Promotes root development and flowering",
      notes: "Work into soil rather than surface application for best results"
    });
  }
  
  // Check potassium levels
  if (soilData.potassium < 40) {
    recommendations.push({
      name: "Potassium-rich Fertilizer",
      amount: "1-2 lbs per 1000 sq ft",
      frequency: "Every 6-8 weeks",
      benefits: "Improves overall plant vigor and disease resistance",
      notes: "Particularly important for fruit and root development"
    });
  }
  
  // Check organic matter
  if (soilData.organicMatter < 3) {
    recommendations.push({
      name: "Compost",
      amount: "1-2 inches layer",
      frequency: "Twice per year",
      benefits: "Improves soil structure, water retention, and nutrient availability",
      notes: "Work into top 4-6 inches of soil if possible"
    });
  }
  
  // Soil type specific recommendations
  switch(soilData.soilType) {
    case SoilType.CLAY:
      recommendations.push({
        name: "Gypsum",
        amount: "40 lbs per 1000 sq ft",
        frequency: "Once per year",
        benefits: "Improves clay soil structure and drainage",
        notes: "Best applied in fall or early spring"
      });
      break;
    
    case SoilType.SANDY:
      recommendations.push({
        name: "Organic Mulch",
        amount: "3-4 inch layer",
        frequency: "Twice per year",
        benefits: "Improves water retention in sandy soils",
        notes: "Reapply as it breaks down"
      });
      break;
      
    default:
      // No specific recommendation for other soil types
      break;
  }
  
  // If the soil is generally healthy, add a maintenance recommendation
  if (calculateSoilHealthScore(soilData) > 7) {
    recommendations.push({
      name: "Balanced Fertilizer (10-10-10)",
      amount: "1 lb per 1000 sq ft",
      frequency: "Every 8-10 weeks during growing season",
      benefits: "Maintains overall soil fertility",
      notes: "Reduce frequency for native plants"
    });
  }
  
  return recommendations;
}

// Create a full analysis object
export function createAnalysis(soilData: SoilData): Analysis {
  return {
    id: `analysis-${Date.now().toString(36)}`,
    date: new Date(),
    soilData: soilData,
    recommendations: generateRecommendations(soilData),
    healthScore: calculateSoilHealthScore(soilData)
  };
}

// Get visual representation of soil health
export function getSoilHealthColor(score: number): string {
  if (score >= 8) return 'bg-soil-green';
  if (score >= 6) return 'bg-soil-light-green';
  if (score >= 4) return 'bg-soil-light-brown';
  if (score >= 2) return 'bg-soil-terra';
  return 'bg-red-600';
}

// Get text description of soil health
export function getSoilHealthDescription(score: number): string {
  if (score >= 8) return 'Excellent';
  if (score >= 6) return 'Good';
  if (score >= 4) return 'Fair';
  if (score >= 2) return 'Poor';
  return 'Very Poor';
}
