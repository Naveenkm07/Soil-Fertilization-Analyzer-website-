
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from '../hooks/use-toast';
import { Button } from '@/components/ui/button';
import SoilAnalyzerHeader from '../components/SoilAnalyzerHeader';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

const Dashboard = () => {
  const navigate = useNavigate();
  const [farmerEmail, setFarmerEmail] = useState<string | null>(null);

  useEffect(() => {
    // Check if user is logged in
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    const email = localStorage.getItem('farmerEmail');
    
    if (!isLoggedIn) {
      toast({
        title: 'Authentication required',
        description: 'Please log in to access your dashboard.',
        variant: 'destructive',
      });
      navigate('/login');
    } else {
      setFarmerEmail(email);
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('farmerEmail');
    toast({
      title: 'Logged out',
      description: 'You have been successfully logged out.',
    });
    navigate('/login');
  };

  const handleAnalyze = () => {
    navigate('/');
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <SoilAnalyzerHeader />
        <div className="flex items-center gap-4">
          <span className="text-sm text-muted-foreground">
            {farmerEmail && `Logged in as: ${farmerEmail}`}
          </span>
          <Button 
            variant="outline" 
            onClick={handleLogout}
            className="border-soil-green text-soil-green hover:bg-soil-green/10"
          >
            Logout
          </Button>
        </div>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <Card>
          <CardHeader>
            <CardTitle>Soil Analysis</CardTitle>
            <CardDescription>Start a new soil analysis or view your recent results</CardDescription>
          </CardHeader>
          <CardContent>
            <Button 
              onClick={handleAnalyze} 
              className="w-full bg-soil-green hover:bg-soil-green/90"
            >
              Start New Analysis
            </Button>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader>
            <CardTitle>Farm Statistics</CardTitle>
            <CardDescription>Overview of your farm's soil health</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="text-center py-8">
              <p className="text-muted-foreground">No analysis data available yet.</p>
              <p className="text-sm text-muted-foreground mt-2">Complete your first soil analysis to see statistics.</p>
            </div>
          </CardContent>
        </Card>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle>Recent Analyses</CardTitle>
          <CardDescription>Your most recent soil analyses and recommendations</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="text-center py-12">
            <p className="text-muted-foreground">No recent analyses found.</p>
            <p className="text-sm text-muted-foreground mt-2">Your analyses will appear here after you complete them.</p>
            <Button 
              onClick={handleAnalyze} 
              className="mt-4 bg-soil-green hover:bg-soil-green/90"
            >
              Perform Your First Analysis
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default Dashboard;
