
import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';

const SoilAnalyzerHeader: React.FC = () => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

  return (
    <header className="bg-soil-green text-white p-6 rounded-lg shadow-md mb-8">
      <div className="container mx-auto flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Soil Fertilization Analyzer</h1>
          <p className="mt-2 opacity-90">Analyze your soil conditions and get fertilizer recommendations</p>
        </div>
        <div>
          {isLoggedIn ? (
            <Link to="/dashboard">
              <Button variant="outline" className="border-white text-white hover:bg-white/20">
                Dashboard
              </Button>
            </Link>
          ) : (
            <Link to="/login">
              <Button variant="outline" className="border-white text-white hover:bg-white/20">
                Farmer Login
              </Button>
            </Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default SoilAnalyzerHeader;
