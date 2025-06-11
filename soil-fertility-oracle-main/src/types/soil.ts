
export interface SoilData {
  id: string;
  date: Date;
  ph: number;
  nitrogen: number;
  phosphorus: number;
  potassium: number;
  organicMatter: number;
  moisture: number;
  soilType: SoilType;
}

export enum SoilType {
  CLAY = "Clay",
  SANDY = "Sandy",
  LOAM = "Loam",
  SILT = "Silt",
  PEAT = "Peat",
  CHALKY = "Chalky"
}

export interface FertilizerRecommendation {
  name: string;
  amount: string;
  frequency: string;
  benefits: string;
  notes?: string;
}

export interface Analysis {
  id: string;
  date: Date;
  soilData: SoilData;
  recommendations: FertilizerRecommendation[];
  healthScore: number;
}
