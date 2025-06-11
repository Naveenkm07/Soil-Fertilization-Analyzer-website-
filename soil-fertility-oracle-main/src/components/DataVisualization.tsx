
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { Analysis } from '../types/soil';

interface DataVisualizationProps {
  analysis: Analysis;
}

const DataVisualization: React.FC<DataVisualizationProps> = ({ analysis }) => {
  // Prepare data for nutrients chart
  const nutrientData = [
    { name: 'Nitrogen', value: analysis.soilData.nitrogen, fill: '#4F7942' },
    { name: 'Phosphorus', value: analysis.soilData.phosphorus, fill: '#8B4513' },
    { name: 'Potassium', value: analysis.soilData.potassium, fill: '#CD5C5C' },
    { name: 'Organic Matter', value: analysis.soilData.organicMatter * 10, fill: '#8FBC8F' } // scaling up for visibility
  ];

  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold mb-4">Soil Composition</h2>
      <Card>
        <CardHeader>
          <CardTitle>Nutrient Levels</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={nutrientData}
                margin={{
                  top: 20,
                  right: 30,
                  left: 20,
                  bottom: 5,
                }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis label={{ value: 'PPM / %*10', angle: -90, position: 'insideLeft' }} />
                <Tooltip />
                <Legend />
                <Bar dataKey="value" name="Level" fill="#8B4513" />
              </BarChart>
            </ResponsiveContainer>
          </div>
          <p className="text-xs text-center mt-2 text-muted-foreground">
            *Organic matter values are multiplied by 10 for better visualization
          </p>
        </CardContent>
      </Card>
    </div>
  );
};

export default DataVisualization;
