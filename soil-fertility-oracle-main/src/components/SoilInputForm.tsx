
import React, { useState, FormEvent } from 'react';
import { SoilType, SoilData } from '../types/soil';
import { Button } from './ui/button';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Slider } from './ui/slider';

interface SoilInputFormProps {
  onAnalyze: (soilData: SoilData) => void;
}

const SoilInputForm: React.FC<SoilInputFormProps> = ({ onAnalyze }) => {
  const [formData, setFormData] = useState<Omit<SoilData, 'id' | 'date'>>({
    ph: 6.5,
    nitrogen: 50,
    phosphorus: 40,
    potassium: 45,
    organicMatter: 3,
    moisture: 60,
    soilType: SoilType.LOAM
  });

  const handleChange = (field: keyof typeof formData, value: any) => {
    setFormData({
      ...formData,
      [field]: value
    });
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    
    const soilData: SoilData = {
      ...formData,
      id: `soil-${Date.now().toString(36)}`,
      date: new Date()
    };
    
    onAnalyze(soilData);
  };

  return (
    <Card className="mb-8">
      <CardHeader className="bg-soil-light-brown bg-opacity-10">
        <CardTitle className="text-soil-brown">Soil Parameters</CardTitle>
      </CardHeader>
      <CardContent className="pt-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <Label htmlFor="ph" className="flex justify-between">
                <span>Soil pH</span>
                <span className="font-mono bg-secondary px-2 rounded-md">{formData.ph}</span>
              </Label>
              <Slider 
                id="ph"
                min={3.5}
                max={9.5}
                step={0.1}
                defaultValue={[formData.ph]}
                onValueChange={([value]) => handleChange('ph', value)}
              />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>Acidic (3.5)</span>
                <span>Neutral (7)</span>
                <span>Alkaline (9.5)</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="soilType">Soil Type</Label>
              <Select 
                value={formData.soilType}
                onValueChange={(value) => handleChange('soilType', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select soil type" />
                </SelectTrigger>
                <SelectContent>
                  {Object.values(SoilType).map((type) => (
                    <SelectItem key={type} value={type}>
                      {type}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="nitrogen" className="flex justify-between">
                <span>Nitrogen (ppm)</span>
                <span className="font-mono bg-secondary px-2 rounded-md">{formData.nitrogen}</span>
              </Label>
              <Slider 
                id="nitrogen"
                min={0}
                max={100}
                step={1}
                defaultValue={[formData.nitrogen]}
                onValueChange={([value]) => handleChange('nitrogen', value)}
              />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>Low</span>
                <span>High</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="phosphorus" className="flex justify-between">
                <span>Phosphorus (ppm)</span>
                <span className="font-mono bg-secondary px-2 rounded-md">{formData.phosphorus}</span>
              </Label>
              <Slider 
                id="phosphorus"
                min={0}
                max={100}
                step={1}
                defaultValue={[formData.phosphorus]}
                onValueChange={([value]) => handleChange('phosphorus', value)}
              />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>Low</span>
                <span>High</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="potassium" className="flex justify-between">
                <span>Potassium (ppm)</span>
                <span className="font-mono bg-secondary px-2 rounded-md">{formData.potassium}</span>
              </Label>
              <Slider 
                id="potassium"
                min={0}
                max={100}
                step={1}
                defaultValue={[formData.potassium]}
                onValueChange={([value]) => handleChange('potassium', value)}
              />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>Low</span>
                <span>High</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="organicMatter" className="flex justify-between">
                <span>Organic Matter (%)</span>
                <span className="font-mono bg-secondary px-2 rounded-md">{formData.organicMatter}</span>
              </Label>
              <Slider 
                id="organicMatter"
                min={0}
                max={10}
                step={0.1}
                defaultValue={[formData.organicMatter]}
                onValueChange={([value]) => handleChange('organicMatter', value)}
              />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>Low</span>
                <span>High</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="moisture" className="flex justify-between">
                <span>Moisture (%)</span>
                <span className="font-mono bg-secondary px-2 rounded-md">{formData.moisture}</span>
              </Label>
              <Slider 
                id="moisture"
                min={0}
                max={100}
                step={1}
                defaultValue={[formData.moisture]}
                onValueChange={([value]) => handleChange('moisture', value)}
              />
              <div className="flex justify-between text-xs text-muted-foreground">
                <span>Dry</span>
                <span>Optimal</span>
                <span>Wet</span>
              </div>
            </div>
          </div>

          <div className="flex justify-end">
            <Button className="bg-soil-green hover:bg-soil-green/90 text-white" type="submit">
              Analyze Soil
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default SoilInputForm;
