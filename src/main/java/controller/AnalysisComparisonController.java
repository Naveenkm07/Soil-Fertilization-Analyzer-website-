package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import model.SoilAnalysis;
import util.DataManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class AnalysisComparisonController {
    @FXML private ComboBox<SoilAnalysis> currentAnalysisCombo;
    @FXML private ComboBox<SoilAnalysis> compareAnalysisCombo;
    @FXML private BarChart<String, Number> phChart;
    @FXML private LineChart<String, Number> nutrientsChart;
    @FXML private LineChart<String, Number> trendChart;
    @FXML private TableView<ComparisonRow> comparisonTable;
    @FXML private TableColumn<ComparisonRow, String> parameterColumn;
    @FXML private TableColumn<ComparisonRow, String> currentValueColumn;
    @FXML private TableColumn<ComparisonRow, String> compareValueColumn;
    @FXML private TableColumn<ComparisonRow, String> differenceColumn;
    @FXML private TableColumn<ComparisonRow, String> statusColumn;
    @FXML private TextArea currentRecommendations;
    @FXML private TextArea compareRecommendations;
    
    private ObservableList<ComparisonRow> comparisonData = FXCollections.observableArrayList();
    private List<SoilAnalysis> allAnalyses;
    
    @FXML
    private void initialize() {
        // Initialize table columns
        parameterColumn.setCellValueFactory(cellData -> cellData.getValue().parameterProperty());
        currentValueColumn.setCellValueFactory(cellData -> cellData.getValue().currentValueProperty());
        compareValueColumn.setCellValueFactory(cellData -> cellData.getValue().compareValueProperty());
        differenceColumn.setCellValueFactory(cellData -> cellData.getValue().differenceProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        // Add cell factories for status column
        statusColumn.setCellFactory(column -> new TableCell<ComparisonRow, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "improved":
                            setStyle("-fx-text-fill: #28a745;");
                            break;
                        case "declined":
                            setStyle("-fx-text-fill: #dc3545;");
                            break;
                        case "unchanged":
                            setStyle("-fx-text-fill: #6c757d;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        comparisonTable.setItems(comparisonData);
        
        // Load analyses
        loadAnalyses();
        
        // Style charts
        styleCharts();
    }
    
    private void loadAnalyses() {
        try {
            allAnalyses = DataManager.getAllAnalyses();
            ObservableList<SoilAnalysis> analysisList = FXCollections.observableArrayList(allAnalyses);
            currentAnalysisCombo.setItems(analysisList);
            compareAnalysisCombo.setItems(analysisList);
            
            // Sort analyses by date
            analysisList.sort((a1, a2) -> 
                LocalDate.parse(a2.getDate()).compareTo(LocalDate.parse(a1.getDate())));
            
            // Select most recent analysis as current
            if (!analysisList.isEmpty()) {
                currentAnalysisCombo.setValue(analysisList.get(0));
                if (analysisList.size() > 1) {
                    compareAnalysisCombo.setValue(analysisList.get(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading analyses", e.getMessage());
        }
    }
    
    private void styleCharts() {
        // Style pH chart
        phChart.setTitle("pH Level Comparison");
        phChart.setLegendVisible(true);
        phChart.setAnimated(true);
        
        // Style nutrients chart
        nutrientsChart.setTitle("Nutrients Comparison");
        nutrientsChart.setLegendVisible(true);
        nutrientsChart.setAnimated(true);
        
        // Style trend chart
        trendChart.setTitle("Parameter Trends");
        trendChart.setLegendVisible(true);
        trendChart.setAnimated(true);
        
        // Add CSS classes
        phChart.getStyleClass().add("chart");
        nutrientsChart.getStyleClass().add("chart");
        trendChart.getStyleClass().add("chart");
    }
    
    @FXML
    private void handleAnalysisSelection() {
        SoilAnalysis current = currentAnalysisCombo.getValue();
        SoilAnalysis compare = compareAnalysisCombo.getValue();
        
        if (current != null && compare != null) {
            updateComparison(current, compare);
            updateCharts(current, compare);
            updateTrendChart();
        }
    }
    
    private void updateComparison(SoilAnalysis current, SoilAnalysis compare) {
        comparisonData.clear();
        
        // Add pH comparison
        addComparisonRow("pH Level", 
            String.format("%.1f", current.getPh()),
            String.format("%.1f", compare.getPh()),
            getDifference(current.getPh(), compare.getPh()),
            getStatus(current.getPh(), compare.getPh(), true));
        
        // Add nutrients comparison
        addComparisonRow("Nitrogen", 
            String.format("%.1f mg/kg", current.getNitrogen()),
            String.format("%.1f mg/kg", compare.getNitrogen()),
            getDifference(current.getNitrogen(), compare.getNitrogen()),
            getStatus(current.getNitrogen(), compare.getNitrogen(), false));
        
        addComparisonRow("Phosphorus", 
            String.format("%.1f mg/kg", current.getPhosphorus()),
            String.format("%.1f mg/kg", compare.getPhosphorus()),
            getDifference(current.getPhosphorus(), compare.getPhosphorus()),
            getStatus(current.getPhosphorus(), compare.getPhosphorus(), false));
        
        addComparisonRow("Potassium", 
            String.format("%.1f mg/kg", current.getPotassium()),
            String.format("%.1f mg/kg", compare.getPotassium()),
            getDifference(current.getPotassium(), compare.getPotassium()),
            getStatus(current.getPotassium(), compare.getPotassium(), false));
        
        // Add organic matter and moisture comparison
        addComparisonRow("Organic Matter", 
            String.format("%.1f%%", current.getOrganicMatter()),
            String.format("%.1f%%", compare.getOrganicMatter()),
            getDifference(current.getOrganicMatter(), compare.getOrganicMatter()),
            getStatus(current.getOrganicMatter(), compare.getOrganicMatter(), false));
        
        addComparisonRow("Moisture", 
            String.format("%.1f%%", current.getMoisture()),
            String.format("%.1f%%", compare.getMoisture()),
            getDifference(current.getMoisture(), compare.getMoisture()),
            getStatus(current.getMoisture(), compare.getMoisture(), false));
        
        // Update recommendations
        currentRecommendations.setText(current.getRecommendations());
        compareRecommendations.setText(compare.getRecommendations());
    }
    
    private void addComparisonRow(String parameter, String currentValue, 
                                String compareValue, String difference, String status) {
        comparisonData.add(new ComparisonRow(parameter, currentValue, compareValue, difference, status));
    }
    
    private String getDifference(double current, double compare) {
        double diff = current - compare;
        return String.format("%.1f", Math.abs(diff)) + 
               (diff >= 0 ? " ↑" : " ↓");
    }
    
    private String getStatus(double current, double compare, boolean isPH) {
        double diff = current - compare;
        double threshold = isPH ? 0.5 : (isPH ? 50 : 10); // Different thresholds for different parameters
        
        if (Math.abs(diff) < threshold) {
            return "Unchanged";
        } else if ((isPH && diff > 0) || (!isPH && diff > 0)) {
            return "Improved";
        } else {
            return "Declined";
        }
    }
    
    private void updateCharts(SoilAnalysis current, SoilAnalysis compare) {
        // Update pH chart
        phChart.getData().clear();
        XYChart.Series<String, Number> phSeries = new XYChart.Series<String, Number>();
        phSeries.setName("pH Level");
        phSeries.getData().add(new XYChart.Data<String, Number>("Current", current.getPh()));
        phSeries.getData().add(new XYChart.Data<String, Number>("Compare", compare.getPh()));
        phChart.getData().add(phSeries);
        
        // Update nutrients chart
        nutrientsChart.getData().clear();
        
        XYChart.Series<String, Number> nitrogenSeries = new XYChart.Series<String, Number>();
        nitrogenSeries.setName("Nitrogen");
        nitrogenSeries.getData().add(new XYChart.Data<String, Number>("Current", current.getNitrogen()));
        nitrogenSeries.getData().add(new XYChart.Data<String, Number>("Compare", compare.getNitrogen()));
        
        XYChart.Series<String, Number> phosphorusSeries = new XYChart.Series<String, Number>();
        phosphorusSeries.setName("Phosphorus");
        phosphorusSeries.getData().add(new XYChart.Data<String, Number>("Current", current.getPhosphorus()));
        phosphorusSeries.getData().add(new XYChart.Data<String, Number>("Compare", compare.getPhosphorus()));
        
        XYChart.Series<String, Number> potassiumSeries = new XYChart.Series<String, Number>();
        potassiumSeries.setName("Potassium");
        potassiumSeries.getData().add(new XYChart.Data<String, Number>("Current", current.getPotassium()));
        potassiumSeries.getData().add(new XYChart.Data<String, Number>("Compare", compare.getPotassium()));
        
        nutrientsChart.getData().addAll(nitrogenSeries, phosphorusSeries, potassiumSeries);
    }
    
    private void updateTrendChart() {
        trendChart.getData().clear();
        
        // Create series for each parameter
        Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
        XYChart.Series<String, Number> phSeries = new XYChart.Series<String, Number>();
        phSeries.setName("pH Level");
        seriesMap.put("pH", phSeries);
        
        XYChart.Series<String, Number> nitrogenSeries = new XYChart.Series<String, Number>();
        nitrogenSeries.setName("Nitrogen");
        seriesMap.put("Nitrogen", nitrogenSeries);
        
        XYChart.Series<String, Number> phosphorusSeries = new XYChart.Series<String, Number>();
        phosphorusSeries.setName("Phosphorus");
        seriesMap.put("Phosphorus", phosphorusSeries);
        
        XYChart.Series<String, Number> potassiumSeries = new XYChart.Series<String, Number>();
        potassiumSeries.setName("Potassium");
        seriesMap.put("Potassium", potassiumSeries);
        
        // Add data points for each analysis
        for (SoilAnalysis analysis : allAnalyses) {
            String date = LocalDate.parse(analysis.getDate())
                .format(DateTimeFormatter.ofPattern("MMM dd"));
            
            seriesMap.get("pH").getData().add(
                new XYChart.Data<String, Number>(date, analysis.getPh()));
            seriesMap.get("Nitrogen").getData().add(
                new XYChart.Data<String, Number>(date, analysis.getNitrogen()));
            seriesMap.get("Phosphorus").getData().add(
                new XYChart.Data<String, Number>(date, analysis.getPhosphorus()));
            seriesMap.get("Potassium").getData().add(
                new XYChart.Data<String, Number>(date, analysis.getPotassium()));
        }
        
        // Add all series to chart
        trendChart.getData().addAll(seriesMap.values());
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnalysisResults.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) currentAnalysisCombo.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), 
                currentAnalysisCombo.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to return to analysis results");
        }
    }
    
    @FXML
    private void handleExport() {
        // TODO: Implement PDF export of comparison
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Comparison");
        alert.setHeaderText("Export Comparison");
        alert.setContentText("Comparison export feature will be implemented soon.");
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        alert.showAndWait();
    }
    
    // Helper class for comparison table data
    public static class ComparisonRow {
        private final javafx.beans.property.SimpleStringProperty parameter;
        private final javafx.beans.property.SimpleStringProperty currentValue;
        private final javafx.beans.property.SimpleStringProperty compareValue;
        private final javafx.beans.property.SimpleStringProperty difference;
        private final javafx.beans.property.SimpleStringProperty status;
        
        public ComparisonRow(String parameter, String currentValue, 
                           String compareValue, String difference, String status) {
            this.parameter = new javafx.beans.property.SimpleStringProperty(parameter);
            this.currentValue = new javafx.beans.property.SimpleStringProperty(currentValue);
            this.compareValue = new javafx.beans.property.SimpleStringProperty(compareValue);
            this.difference = new javafx.beans.property.SimpleStringProperty(difference);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }
        
        public javafx.beans.property.StringProperty parameterProperty() { return parameter; }
        public javafx.beans.property.StringProperty currentValueProperty() { return currentValue; }
        public javafx.beans.property.StringProperty compareValueProperty() { return compareValue; }
        public javafx.beans.property.StringProperty differenceProperty() { return difference; }
        public javafx.beans.property.StringProperty statusProperty() { return status; }
    }
} 