
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from './ui/card';
import { FertilizerRecommendation } from '../types/soil';
import { Leaf, Sprout } from 'lucide-react';

interface FertilizerRecommendationsProps {
  recommendations: FertilizerRecommendation[];
}

const FertilizerRecommendations: React.FC<FertilizerRecommendationsProps> = ({ recommendations }) => {
  if (recommendations.length === 0) {
    return (
      <Card className="mb-8">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Leaf className="h-5 w-5 text-soil-green" />
            Fertilizer Recommendations
          </CardTitle>
          <CardDescription>Your soil is healthy and doesn't need additional fertilizer at this time.</CardDescription>
        </CardHeader>
      </Card>
    );
  }
  
  // Group recommendations by priority/type
  const essentialRecommendations = recommendations.filter(r => 
    r.name.toLowerCase().includes('nitrogen') || 
    r.name.toLowerCase().includes('phosph') || 
    r.name.toLowerCase().includes('potassium') ||
    r.name.toLowerCase().includes('lime') ||
    r.name.toLowerCase().includes('sulfur')
  );
  
  const supplementalRecommendations = recommendations.filter(r => 
    !essentialRecommendations.includes(r)
  );
  
  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
        <Leaf className="h-6 w-6 text-soil-green" />
        Fertilizer Recommendations
      </h2>
      
      {essentialRecommendations.length > 0 && (
        <>
          <h3 className="text-lg font-medium mb-3 text-soil-green">Essential Nutrients</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            {essentialRecommendations.map((recommendation, index) => (
              <RecommendationCard key={index} recommendation={recommendation} />
            ))}
          </div>
        </>
      )}
      
      {supplementalRecommendations.length > 0 && (
        <>
          <h3 className="text-lg font-medium mb-3 text-soil-green">Soil Amendments & Supplements</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {supplementalRecommendations.map((recommendation, index) => (
              <RecommendationCard key={index} recommendation={recommendation} />
            ))}
          </div>
        </>
      )}
    </div>
  );
};

// Separate card component for each recommendation
const RecommendationCard: React.FC<{ recommendation: FertilizerRecommendation }> = ({ recommendation }) => {
  return (
    <Card className="border-l-4 border-soil-green hover:shadow-md transition-shadow">
      <CardHeader className="pb-2">
        <CardTitle className="text-lg flex items-center gap-2">
          <Sprout className="h-4 w-4 text-soil-green" />
          {recommendation.name}
        </CardTitle>
        <CardDescription className="font-medium">
          {recommendation.amount} • {recommendation.frequency}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p className="mb-2">{recommendation.benefits}</p>
        {recommendation.notes && (
          <p className="text-sm text-muted-foreground italic border-l-2 border-muted pl-3 mt-2">
            <span className="font-medium">Note:</span> {recommendation.notes}
          </p>
        )}
      </CardContent>
    </Card>
  );
};

export default FertilizerRecommendations;
