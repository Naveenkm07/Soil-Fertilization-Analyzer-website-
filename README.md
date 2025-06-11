# 🌾 Soil Nutrient Analysis and Fertilizer Recommendation System

A beautifully designed, fully-featured JavaFX desktop application for soil nutrient analysis, fertilizer recommendations, and farm management.

## 🖥️ Tech Stack
- Java 17 (JavaFX for GUI)
- SQLite (embedded DB)
- iText (PDF generation)
- JFoenix (Material Design for JavaFX)
- HTML/CSS/JS (for web view reports, forms, interactive UI)
- Material Design / Tailwind CSS (for front-end styling)

## 🎯 Key Features
- Login/Register system (with validation and user roles)
- Modern dashboard with real-time data cards
- Soil nutrient input form (NPK sliders/fields)
- Automated fertilizer recommendation engine
- Dynamic PDF report generator
- Interactive charts (NPK vs optimal)
- Soil report history
- Profile page with user info and soil region selection
- Settings (theme switch, language, units)
- Help & Support page
- Multi-language support
- Seasonal fertilizer planner
- AI tips on crop rotation & soil care
- Fertilizer cost estimator
- Weather API integration
- Export as Excel & PDF
- Offline-first design

## 📁 Project Structure
```
SoilNutrientAnalysisApp/
│
├─ src/
│   ├─ main/
│   │   ├─ java/
│   │   │   ├─ app/                # Main app launcher
│   │   │   ├─ controller/         # JavaFX controllers
│   │   │   ├─ model/              # Data models
│   │   │   ├─ service/            # Business logic, DB, PDF, AI, Weather
│   │   │   └─ util/               # Utilities
│   │   │
│   │   └─ resources/
│   │       ├─ fxml/               # FXML layouts
│   │       ├─ css/                # Custom styles
│   │       ├─ images/             # Logos, icons, banners
│   │       └─ i18n/               # Language files
│   │
│   └─ test/
│
├─ assets/                         # HTML/CSS/JS for web views
├─ db/                             # Sample SQLite DB
├─ README.md
└─ pom.xml
```

## 🚀 Getting Started
1. **Clone the repository**
2. **Install Java 17+ and Maven**
3. **Build the project:**
   ```
   mvn clean package
   ```
4. **Run the app:**
   ```
   mvn javafx:run
   ```

## 📦 Sample Data
- The `db/` folder contains a sample SQLite database with preloaded users and soil entries.

## 🖼️ Screenshots
- Screenshots of each screen are provided in the `assets/` folder.

## 📚 License
MIT 