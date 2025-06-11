package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.SoilAnalysis;
import model.User;
import util.DatabaseUtil;
import java.time.LocalDate;
import java.util.Arrays;

public class NewAnalysisController implements UserAwareController {
    @FXML private JFXTextField locationField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private JFXTextArea descriptionArea;
    @FXML private JFXTextField phField;
    @FXML private JFXTextField nitrogenField;
    @FXML private JFXTextField phosphorusField;
    @FXML private JFXTextField potassiumField;
    @FXML private JFXTextField organicMatterField;
    @FXML private JFXTextField moistureField;
    @FXML private Label errorLabel;

    private User currentUser;

    @FXML
    private void initialize() {
        // Initialize analysis types
        typeComboBox.getItems().addAll(
            "Basic Analysis",
            "Detailed Analysis",
            "Nutrient Analysis",
            "Organic Matter Analysis",
            "Complete Analysis"
        );

        // Add input validation
        addNumericValidation(phField, 0, 14);
        addNumericValidation(nitrogenField, 0, 1000);
        addNumericValidation(phosphorusField, 0, 1000);
        addNumericValidation(potassiumField, 0, 1000);
        addNumericValidation(organicMatterField, 0, 100);
        addNumericValidation(moistureField, 0, 100);
    }

    private void addNumericValidation(JFXTextField field, double min, double max) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    double value = Double.parseDouble(newValue);
                    if (value < min || value > max) {
                        field.setStyle("-fx-text-fill: red;");
                    } else {
                        field.setStyle("-fx-text-fill: black;");
                    }
                } catch (NumberFormatException e) {
                    field.setStyle("-fx-text-fill: red;");
                }
            } else {
                field.setStyle("-fx-text-fill: black;");
            }
        });
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleAnalyze() {
        // Clear previous error
        errorLabel.setText("");

        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            // Create soil analysis object
            SoilAnalysis analysis = new SoilAnalysis(
                0, // ID will be set by database
                currentUser.getId(),
                LocalDate.now().toString(),
                typeComboBox.getValue(),
                descriptionArea.getText(),
                "Pending",
                Double.parseDouble(phField.getText()),
                Double.parseDouble(nitrogenField.getText()),
                Double.parseDouble(phosphorusField.getText()),
                Double.parseDouble(potassiumField.getText()),
                Double.parseDouble(organicMatterField.getText()),
                Double.parseDouble(moistureField.getText()),
                locationField.getText(),
                generateRecommendations()
            );

            // Save to database
            if (DatabaseUtil.saveSoilAnalysis(analysis)) {
                // Show results screen
                showResults(analysis);
            } else {
                errorLabel.setText("Failed to save analysis. Please try again.");
            }
        } catch (Exception e) {
            errorLabel.setText("Error processing analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        // Check required fields
        if (locationField.getText().isEmpty()) {
            errorLabel.setText("Please enter a location.");
            return false;
        }
        if (typeComboBox.getValue() == null) {
            errorLabel.setText("Please select an analysis type.");
            return false;
        }

        // Validate numeric fields
        try {
            double ph = Double.parseDouble(phField.getText());
            double nitrogen = Double.parseDouble(nitrogenField.getText());
            double phosphorus = Double.parseDouble(phosphorusField.getText());
            double potassium = Double.parseDouble(potassiumField.getText());
            double organicMatter = Double.parseDouble(organicMatterField.getText());
            double moisture = Double.parseDouble(moistureField.getText());

            if (ph < 0 || ph > 14) {
                errorLabel.setText("pH must be between 0 and 14.");
                return false;
            }
            if (nitrogen < 0 || nitrogen > 1000) {
                errorLabel.setText("Nitrogen must be between 0 and 1000 mg/kg.");
                return false;
            }
            if (phosphorus < 0 || phosphorus > 1000) {
                errorLabel.setText("Phosphorus must be between 0 and 1000 mg/kg.");
                return false;
            }
            if (potassium < 0 || potassium > 1000) {
                errorLabel.setText("Potassium must be between 0 and 1000 mg/kg.");
                return false;
            }
            if (organicMatter < 0 || organicMatter > 100) {
                errorLabel.setText("Organic matter must be between 0 and 100%.");
                return false;
            }
            if (moisture < 0 || moisture > 100) {
                errorLabel.setText("Moisture must be between 0 and 100%.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter valid numbers for all soil parameters.");
            return false;
        }

        return true;
    }

    private String generateRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        double ph = Double.parseDouble(phField.getText());
        double nitrogen = Double.parseDouble(nitrogenField.getText());
        double phosphorus = Double.parseDouble(phosphorusField.getText());
        double potassium = Double.parseDouble(potassiumField.getText());
        double organicMatter = Double.parseDouble(organicMatterField.getText());

        // pH recommendations
        if (ph < 6.0) {
            recommendations.append("• Soil is acidic. Consider applying lime to raise pH.\n");
        } else if (ph > 7.5) {
            recommendations.append("• Soil is alkaline. Consider applying sulfur to lower pH.\n");
        }

        // Nutrient recommendations
        if (nitrogen < 40) {
            recommendations.append("• Low nitrogen levels. Apply nitrogen-rich fertilizer.\n");
        }
        if (phosphorus < 20) {
            recommendations.append("• Low phosphorus levels. Apply phosphate fertilizer.\n");
        }
        if (potassium < 150) {
            recommendations.append("• Low potassium levels. Apply potash fertilizer.\n");
        }

        // Organic matter recommendations
        if (organicMatter < 3.0) {
            recommendations.append("• Low organic matter. Add compost or organic amendments.\n");
        }

        return recommendations.toString();
    }

    private void showResults(SoilAnalysis analysis) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnalysisResults.fxml"));
            Parent root = loader.load();
            
            // Set the analysis data
            AnalysisResultsController controller = loader.getController();
            controller.setAnalysis(analysis);
            
            // Get the current stage
            Stage stage = (Stage) locationField.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error showing results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Return to dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) locationField.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error returning to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 