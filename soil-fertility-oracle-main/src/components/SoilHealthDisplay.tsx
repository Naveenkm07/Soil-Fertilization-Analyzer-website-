
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { getSoilHealthColor, getSoilHealthDescription } from '../utils/soilAnalyzer';
import { Analysis } from '../types/soil';

interface SoilHealthDisplayProps {
  analysis: Analysis;
}

const SoilHealthDisplay: React.FC<SoilHealthDisplayProps> = ({ analysis }) => {
  const healthColor = getSoilHealthColor(analysis.healthScore);
  const healthDescription = getSoilHealthDescription(analysis.healthScore);
  
  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold mb-4">Soil Health Analysis</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-lg">Health Score</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-center h-32">
              <div className="relative w-32 h-32">
                <div className="absolute inset-2 rounded-full bg-muted flex items-center justify-center">
                  <span className="text-4xl font-bold">{analysis.healthScore}</span>
                </div>
                <svg className="w-full h-full" viewBox="0 0 36 36">
                  <path
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke={healthColor.replace('bg-', 'stroke-')}
                    strokeWidth="3"
                    strokeDasharray={`${analysis.healthScore * 10}, 100`}
                    className="stroke-current"
                  />
                </svg>
              </div>
            </div>
            <div className={`text-center mt-2 font-semibold ${healthColor.replace('bg-', 'text-')}`}>
              {healthDescription}
            </div>
          </CardContent>
        </Card>

        <Card className="col-span-1 md:col-span-2">
          <CardHeader className="pb-2">
            <CardTitle className="text-lg">Soil Properties</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">pH Level</p>
                <p className="font-medium">{analysis.soilData.ph}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Soil Type</p>
                <p className="font-medium">{analysis.soilData.soilType}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Nitrogen</p>
                <p className="font-medium">{analysis.soilData.nitrogen} ppm</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Phosphorus</p>
                <p className="font-medium">{analysis.soilData.phosphorus} ppm</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Potassium</p>
                <p className="font-medium">{analysis.soilData.potassium} ppm</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Organic Matter</p>
                <p className="font-medium">{analysis.soilData.organicMatter}%</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Moisture</p>
                <p className="font-medium">{analysis.soilData.moisture}%</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Analysis Date</p>
                <p className="font-medium">{analysis.date.toLocaleDateString()}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default SoilHealthDisplay;
