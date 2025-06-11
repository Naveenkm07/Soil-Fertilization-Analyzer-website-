
import React, { useState } from 'react';
import { SoilData, Analysis } from '../types/soil';
import { createAnalysis } from '../utils/soilAnalyzer';
import { toast } from '../hooks/use-toast';

import SoilAnalyzerHeader from '../components/SoilAnalyzerHeader';
import SoilInputForm from '../components/SoilInputForm';
import SoilHealthDisplay from '../components/SoilHealthDisplay';
import FertilizerRecommendations from '../components/FertilizerRecommendations';
import DataVisualization from '../components/DataVisualization';
import AnalysisHistory from '../components/AnalysisHistory';

const Index = () => {
  const [currentAnalysis, setCurrentAnalysis] = useState<Analysis | null>(null);
  const [analysisHistory, setAnalysisHistory] = useState<Analysis[]>([]);

  const handleAnalyzeSoil = (soilData: SoilData) => {
    const analysis = createAnalysis(soilData);
    setCurrentAnalysis(analysis);
    setAnalysisHistory(prevHistory => [analysis, ...prevHistory]);
    
    toast({
      title: "Analysis Complete",
      description: "Your soil analysis has been processed successfully.",
      duration: 3000,
    });
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <SoilAnalyzerHeader />
      
      <div className="grid grid-cols-1 gap-8">
        <SoilInputForm onAnalyze={handleAnalyzeSoil} />
        
        {currentAnalysis && (
          <>
            <SoilHealthDisplay analysis={currentAnalysis} />
            <DataVisualization analysis={currentAnalysis} />
            <FertilizerRecommendations recommendations={currentAnalysis.recommendations} />
          </>
        )}
        
        <AnalysisHistory 
          analyses={analysisHistory} 
          onSelect={(analysis) => {
            setCurrentAnalysis(analysis);
            toast({
              title: "Previous Analysis Loaded",
              description: `Viewing analysis from ${analysis.date.toLocaleDateString()}.`,
              duration: 3000,
            });
          }} 
        />
      </div>
    </div>
  );
};

export default Index;
