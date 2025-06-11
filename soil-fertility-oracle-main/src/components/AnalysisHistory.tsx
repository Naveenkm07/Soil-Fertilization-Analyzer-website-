
import React from 'react';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Button } from './ui/button';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Analysis } from '../types/soil';
import { getSoilHealthDescription, getSoilHealthColor } from '../utils/soilAnalyzer';

interface AnalysisHistoryProps {
  analyses: Analysis[];
  onSelect: (analysis: Analysis) => void;
}

const AnalysisHistory: React.FC<AnalysisHistoryProps> = ({ analyses, onSelect }) => {
  if (analyses.length === 0) {
    return null;
  }
  
  return (
    <Card className="mb-8">
      <CardHeader>
        <CardTitle>Analysis History</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Soil Type</TableHead>
                <TableHead>pH</TableHead>
                <TableHead>Health Score</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {analyses.map((analysis) => {
                const healthColor = getSoilHealthColor(analysis.healthScore);
                const healthDescription = getSoilHealthDescription(analysis.healthScore);
                
                return (
                  <TableRow key={analysis.id}>
                    <TableCell>{analysis.date.toLocaleDateString()}</TableCell>
                    <TableCell>{analysis.soilData.soilType}</TableCell>
                    <TableCell>{analysis.soilData.ph}</TableCell>
                    <TableCell>{analysis.healthScore}</TableCell>
                    <TableCell>
                      <span className={`px-2 py-1 rounded-full text-xs ${healthColor} text-white`}>
                        {healthDescription}
                      </span>
                    </TableCell>
                    <TableCell>
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => onSelect(analysis)}
                      >
                        View
                      </Button>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  );
};

export default AnalysisHistory;
