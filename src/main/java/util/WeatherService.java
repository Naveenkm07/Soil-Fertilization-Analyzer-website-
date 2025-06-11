package util;

import java.time.LocalDateTime;
import java.util.Random;

public class WeatherService {
    private static final Random random = new Random();
    private LocalDateTime lastUpdate;
    private WeatherData cachedData;

    public WeatherData getCurrentWeather() {
        // In a real application, this would fetch data from a weather API
        // For now, we'll simulate weather data with random values
        if (cachedData != null && lastUpdate != null && 
            LocalDateTime.now().minusMinutes(30).isBefore(lastUpdate)) {
            return cachedData;
        }

        double temperature = 15 + random.nextDouble() * 20; // 15-35°C
        double soilMoisture = 20 + random.nextDouble() * 40; // 20-60%
        String[] descriptions = {"Sunny", "Partly Cloudy", "Cloudy", "Light Rain"};
        String description = descriptions[random.nextInt(descriptions.length)];
        String iconCode = getIconCode(description);

        cachedData = new WeatherData(temperature, soilMoisture, description, iconCode);
        lastUpdate = LocalDateTime.now();
        return cachedData;
    }

    private String getIconCode(String description) {
        switch (description.toLowerCase()) {
            case "sunny": return "sunny";
            case "partly cloudy": return "partly-cloudy";
            case "cloudy": return "cloudy";
            case "light rain": return "rain";
            default: return "default";
        }
    }

    public static class WeatherData {
        private final double temperature;
        private final double soilMoisture;
        private final String description;
        private final String iconCode;

        public WeatherData(double temperature, double soilMoisture, String description, String iconCode) {
            this.temperature = temperature;
            this.soilMoisture = soilMoisture;
            this.description = description;
            this.iconCode = iconCode;
        }

        public double getTemperature() { return temperature; }
        public double getSoilMoisture() { return soilMoisture; }
        public String getDescription() { return description; }
        public String getIconCode() { return iconCode; }
    }
} 