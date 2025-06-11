package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import model.SoilAnalysis;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.io.image.ImageDataFactory;
import java.net.URL;
import java.util.Base64;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.ArrayList;
import com.itextpdf.layout.element.Cell;
import javafx.scene.layout.VBox;

public class AnalysisResultsController {
    @FXML private Label analysisTypeLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label phLabel;
    @FXML private Label phStatusLabel;
    @FXML private Label nitrogenLabel;
    @FXML private Label phosphorusLabel;
    @FXML private Label potassiumLabel;
    @FXML private Label organicMatterLabel;
    @FXML private Label moistureLabel;
    @FXML private ProgressBar phProgressBar;
    @FXML private ProgressBar nitrogenProgressBar;
    @FXML private ProgressBar phosphorusProgressBar;
    @FXML private ProgressBar potassiumProgressBar;
    @FXML private ProgressBar organicMatterProgressBar;
    @FXML private ProgressBar moistureProgressBar;
    @FXML private TextArea recommendationsArea;
    @FXML private PieChart nutrientBalanceChart;
    @FXML private JFXButton refreshButton;
    @FXML private JFXButton compareButton;
    @FXML private JFXButton phInfoButton;
    @FXML private JFXButton nutrientsInfoButton;
    @FXML private JFXButton qualityInfoButton;
    @FXML private JFXButton chartToggleButton;
    @FXML private JFXButton copyButton;
    
    private SoilAnalysis currentAnalysis;
    private boolean isPieChartView = true;
    private BarChart<String, Number> nutrientBarChart;

    @FXML
    private void initialize() {
        // Initialize pie chart
        nutrientBalanceChart.setTitle("Nutrient Balance");
        nutrientBalanceChart.setLegendVisible(true);
        nutrientBalanceChart.setLabelsVisible(true);
        nutrientBalanceChart.setStartAngle(90);
        
        // Initialize bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("mg/kg");
        
        nutrientBarChart = new BarChart<>(xAxis, yAxis);
        nutrientBarChart.setTitle("Nutrient Levels");
        nutrientBarChart.setLegendVisible(false);
        nutrientBarChart.setAnimated(true);
        nutrientBarChart.setBarGap(10);
        nutrientBarChart.setCategoryGap(20);
        
        // Style the charts
        nutrientBalanceChart.getStyleClass().add("chart");
        nutrientBarChart.getStyleClass().add("chart");
    }

    public void setAnalysis(SoilAnalysis analysis) {
        this.currentAnalysis = analysis;
        updateUI();
    }

    private void updateUI() {
        if (currentAnalysis == null) return;

        // Update basic information
        analysisTypeLabel.setText(currentAnalysis.getType());
        dateLabel.setText(LocalDate.parse(currentAnalysis.getDate())
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        locationLabel.setText(currentAnalysis.getLocation());

        // Update pH level
        updateParameter(phLabel, phProgressBar, phStatusLabel, 
            currentAnalysis.getPh(), 14.0, "", "pH Level");

        // Update nutrients
        updateParameter(nitrogenLabel, nitrogenProgressBar, null,
            currentAnalysis.getNitrogen(), 1000.0, " mg/kg", "Nitrogen");
        updateParameter(phosphorusLabel, phosphorusProgressBar, null,
            currentAnalysis.getPhosphorus(), 1000.0, " mg/kg", "Phosphorus");
        updateParameter(potassiumLabel, potassiumProgressBar, null,
            currentAnalysis.getPotassium(), 1000.0, " mg/kg", "Potassium");

        // Update organic matter and moisture
        updateParameter(organicMatterLabel, organicMatterProgressBar, null,
            currentAnalysis.getOrganicMatter(), 100.0, "%", "Organic Matter");
        updateParameter(moistureLabel, moistureProgressBar, null,
            currentAnalysis.getMoisture(), 100.0, "%", "Moisture");

        // Update recommendations
        recommendationsArea.setText(currentAnalysis.getRecommendations());

        // Update nutrient balance chart
        updateNutrientBalanceChart();

        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), 
            phLabel.getParent().getParent());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void updateParameter(Label valueLabel, ProgressBar progressBar, Label statusLabel,
                               double value, double max, String unit, String parameter) {
        // Update value label
        valueLabel.setText(String.format("%.1f%s", value, unit));
        
        // Update progress bar
        double progress = value / max;
        progressBar.setProgress(progress);
        
        // Set progress bar color based on value
        if (parameter.equals("pH Level")) {
            if (value >= 6.0 && value <= 7.0) {
                progressBar.setStyle("-fx-accent: #28a745;"); // Optimal
                if (statusLabel != null) statusLabel.setText("Optimal");
            } else if (value >= 5.5 && value <= 7.5) {
                progressBar.setStyle("-fx-accent: #ffc107;"); // Acceptable
                if (statusLabel != null) statusLabel.setText("Acceptable");
            } else {
                progressBar.setStyle("-fx-accent: #dc3545;"); // Poor
                if (statusLabel != null) statusLabel.setText("Needs Attention");
            }
        } else {
            // For other parameters
            if (progress >= 0.7) {
                progressBar.setStyle("-fx-accent: #28a745;"); // Good
            } else if (progress >= 0.4) {
                progressBar.setStyle("-fx-accent: #ffc107;"); // Moderate
            } else {
                progressBar.setStyle("-fx-accent: #dc3545;"); // Poor
            }
        }
    }

    private void updateNutrientBalanceChart() {
        nutrientBalanceChart.getData().clear();
        
        // Calculate total nutrients
        double total = currentAnalysis.getNitrogen() + 
                      currentAnalysis.getPhosphorus() + 
                      currentAnalysis.getPotassium();
        
        // Add data to pie chart
        nutrientBalanceChart.getData().add(new PieChart.Data(
            "Nitrogen", (currentAnalysis.getNitrogen() / total) * 100));
        nutrientBalanceChart.getData().add(new PieChart.Data(
            "Phosphorus", (currentAnalysis.getPhosphorus() / total) * 100));
        nutrientBalanceChart.getData().add(new PieChart.Data(
            "Potassium", (currentAnalysis.getPotassium() / total) * 100));
        
        // Set colors
        nutrientBalanceChart.getData().get(0).getNode().setStyle("-fx-pie-color: #43cea2;");
        nutrientBalanceChart.getData().get(1).getNode().setStyle("-fx-pie-color: #185a9d;");
        nutrientBalanceChart.getData().get(2).getNode().setStyle("-fx-pie-color: #ff6b6b;");
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) analysisTypeLabel.getScene().getWindow();
            
            // Set the new scene with fade transition
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), 
                analysisTypeLabel.getScene().getRoot());
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
        }
    }

    @FXML
    private void handleGenerateReport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("soil_analysis_report.pdf");
            
            File file = fileChooser.showSaveDialog(analysisTypeLabel.getScene().getWindow());
            if (file != null) {
                generatePDFReport(file);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Report Generated");
                success.setContentText("The soil analysis report has been saved successfully.");
                
                DialogPane dialogPane = success.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
                dialogPane.getStyleClass().add("dialog-pane");
                
                success.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Report Generation Failed");
            error.setContentText("An error occurred while generating the report: " + e.getMessage());
            
            DialogPane dialogPane = error.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            
            error.showAndWait();
        }
    }

    private void generatePDFReport(File file) throws Exception {
        PdfWriter writer = new PdfWriter(new FileOutputStream(file));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Add title
        Paragraph title = new Paragraph("Soil Analysis Report")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(24)
            .setBold()
            .setMarginBottom(20);
        document.add(title);
        
        // Add basic information
        Table infoTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(20);
        
        addTableRow(infoTable, "Analysis Type", currentAnalysis.getType());
        addTableRow(infoTable, "Date", LocalDate.parse(currentAnalysis.getDate())
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        addTableRow(infoTable, "Location", currentAnalysis.getLocation());
        
        document.add(infoTable);
        
        // Add soil parameters
        Paragraph paramsTitle = new Paragraph("Soil Parameters")
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(paramsTitle);
        
        Table paramsTable = new Table(3).setWidth(UnitValue.createPercentValue(100));
        paramsTable.setMarginBottom(20);
        
        // Add headers
        paramsTable.addHeaderCell(createHeaderCell("Parameter"));
        paramsTable.addHeaderCell(createHeaderCell("Value"));
        paramsTable.addHeaderCell(createHeaderCell("Status"));
        
        // Add parameter rows
        addParameterRow(paramsTable, "pH Level", 
            String.format("%.1f", currentAnalysis.getPh()),
            getPHStatus(currentAnalysis.getPh()));
        addParameterRow(paramsTable, "Nitrogen", 
            String.format("%.1f mg/kg", currentAnalysis.getNitrogen()),
            getNutrientStatus(currentAnalysis.getNitrogen() / 1000.0));
        addParameterRow(paramsTable, "Phosphorus", 
            String.format("%.1f mg/kg", currentAnalysis.getPhosphorus()),
            getNutrientStatus(currentAnalysis.getPhosphorus() / 1000.0));
        addParameterRow(paramsTable, "Potassium", 
            String.format("%.1f mg/kg", currentAnalysis.getPotassium()),
            getNutrientStatus(currentAnalysis.getPotassium() / 1000.0));
        addParameterRow(paramsTable, "Organic Matter", 
            String.format("%.1f%%", currentAnalysis.getOrganicMatter()),
            getNutrientStatus(currentAnalysis.getOrganicMatter() / 100.0));
        addParameterRow(paramsTable, "Moisture", 
            String.format("%.1f%%", currentAnalysis.getMoisture()),
            getNutrientStatus(currentAnalysis.getMoisture() / 100.0));
        
        document.add(paramsTable);
        
        // Add nutrient balance chart
        Paragraph chartTitle = new Paragraph("Nutrient Balance")
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(chartTitle);
        
        // Capture and add pie chart as image
        WritableImage chartImage = nutrientBalanceChart.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(chartImage, null);
        File tempFile = File.createTempFile("chart", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);
        
        Image chartPdfImage = new Image(ImageDataFactory.create(tempFile.getAbsolutePath()));
        chartPdfImage.setWidth(UnitValue.createPercentValue(50));
        chartPdfImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        document.add(chartPdfImage);
        
        // Add recommendations
        Paragraph recTitle = new Paragraph("Recommendations")
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(recTitle);
        
        Paragraph recommendations = new Paragraph(currentAnalysis.getRecommendations())
            .setMarginBottom(20);
        document.add(recommendations);
        
        // Add footer
        Paragraph footer = new Paragraph("Generated on " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10)
            .setFontColor(ColorConstants.GRAY);
        document.add(footer);
        
        document.close();
        tempFile.delete();
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(createCell(label, true));
        table.addCell(createCell(value, false));
    }

    private Cell createCell(String text, boolean isLabel) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setPadding(5f);
        if (isLabel) {
            cell.setBold();
            cell.setBackgroundColor(new DeviceRgb(240, 240, 240));
        }
        return cell;
    }

    private Cell createHeaderCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setBold();
        cell.setBackgroundColor(new DeviceRgb(200, 200, 200));
        cell.setPadding(5f);
        return cell;
    }

    private void addParameterRow(Table table, String parameter, String value, String status) {
        table.addCell(createCell(parameter, true));
        table.addCell(createCell(value, false));
        table.addCell(createCell(status, false));
    }

    private String getPHStatus(double ph) {
        if (ph >= 6.0 && ph <= 7.0) return "Optimal";
        if (ph >= 5.5 && ph <= 7.5) return "Acceptable";
        return "Needs Attention";
    }

    private String getNutrientStatus(double ratio) {
        if (ratio >= 0.7) return "Good";
        if (ratio >= 0.4) return "Moderate";
        return "Poor";
    }

    @FXML
    private void handleShareResults() {
        try {
            // Capture the current view as an image
            WritableImage snapshot = analysisTypeLabel.getScene().getRoot().snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            
            // Save to temporary file
            File tempFile = File.createTempFile("analysis_share", ".png");
            ImageIO.write(bufferedImage, "png", tempFile);
            
            // Create share dialog
            Dialog<ButtonType> shareDialog = new Dialog<>();
            shareDialog.setTitle("Share Results");
            shareDialog.setHeaderText("Share Analysis Results");
            
            DialogPane dialogPane = shareDialog.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            
            // Add share options
            ButtonType emailButton = new ButtonType("Email");
            ButtonType downloadButton = new ButtonType("Download Image");
            ButtonType cancelButton = ButtonType.CANCEL;
            
            dialogPane.getButtonTypes().addAll(emailButton, downloadButton, cancelButton);
            
            // Handle share options
            shareDialog.showAndWait().ifPresent(response -> {
                if (response == emailButton) {
                    // TODO: Implement email sharing
                    showShareAlert("Email sharing will be implemented soon.");
                } else if (response == downloadButton) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Image");
                    fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("PNG Files", "*.png"));
                    fileChooser.setInitialFileName("soil_analysis.png");
                    
                    File file = fileChooser.showSaveDialog(analysisTypeLabel.getScene().getWindow());
                    if (file != null) {
                        try {
                            ImageIO.write(bufferedImage, "png", file);
                            showShareAlert("Image saved successfully!");
                        } catch (Exception e) {
                            showShareAlert("Error saving image: " + e.getMessage());
                        }
                    }
                }
            });
            
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            showShareAlert("Error sharing results: " + e.getMessage());
        }
    }

    private void showShareAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Share Results");
        alert.setHeaderText("Share Results");
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        // Add refresh animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), 
            analysisTypeLabel.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0.5);
        fadeOut.setOnFinished(e -> {
            updateUI();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), 
                analysisTypeLabel.getScene().getRoot());
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }
    
    @FXML
    private void handleCompare() {
        // TODO: Implement comparison with previous analyses
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compare Analyses");
        alert.setHeaderText("Compare Analyses");
        alert.setContentText("Analysis comparison feature will be implemented soon.");
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        alert.showAndWait();
    }
    
    @FXML
    private void showPHInfo() {
        showInfoDialog("pH Level Information",
            "pH Level indicates the acidity or alkalinity of the soil:\n\n" +
            "• Optimal Range: 6.0 - 7.0\n" +
            "• Acceptable Range: 5.5 - 7.5\n" +
            "• Below 5.5: Too acidic\n" +
            "• Above 7.5: Too alkaline\n\n" +
            "Most plants prefer slightly acidic to neutral soil (pH 6.0-7.0).");
    }
    
    @FXML
    private void showNutrientsInfo() {
        showInfoDialog("Soil Nutrients Information",
            "Essential soil nutrients and their roles:\n\n" +
            "• Nitrogen (N): Promotes leaf growth and green color\n" +
            "• Phosphorus (P): Supports root development and flowering\n" +
            "• Potassium (K): Enhances fruit quality and disease resistance\n\n" +
            "Optimal ranges (mg/kg):\n" +
            "• Nitrogen: 40-80\n" +
            "• Phosphorus: 20-50\n" +
            "• Potassium: 150-250");
    }
    
    @FXML
    private void showQualityInfo() {
        showInfoDialog("Soil Quality Information",
            "Soil quality indicators:\n\n" +
            "• Organic Matter: Improves soil structure and nutrient retention\n" +
            "  - Optimal: 3-5%\n" +
            "  - Good: 2-3%\n" +
            "  - Poor: <2%\n\n" +
            "• Moisture Content: Essential for plant growth\n" +
            "  - Optimal: 25-35%\n" +
            "  - Good: 20-25%\n" +
            "  - Poor: <20% or >35%");
    }
    
    private void showInfoDialog(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);
        textArea.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        
        dialogPane.setContent(textArea);
        
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(closeButton);
        
        dialog.showAndWait();
    }
    
    @FXML
    private void toggleChartView() {
        VBox chartContainer = (VBox) nutrientBalanceChart.getParent();
        chartContainer.getChildren().clear();
        
        if (isPieChartView) {
            // Switch to bar chart
            updateBarChart();
            chartContainer.getChildren().add(nutrientBarChart);
        } else {
            // Switch to pie chart
            updateNutrientBalanceChart();
            chartContainer.getChildren().add(nutrientBalanceChart);
        }
        
        isPieChartView = !isPieChartView;
    }
    
    private void updateBarChart() {
        nutrientBarChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Nitrogen", currentAnalysis.getNitrogen()));
        series.getData().add(new XYChart.Data<>("Phosphorus", currentAnalysis.getPhosphorus()));
        series.getData().add(new XYChart.Data<>("Potassium", currentAnalysis.getPotassium()));
        
        nutrientBarChart.getData().add(series);
        
        // Set colors
        for (XYChart.Data<String, Number> data : series.getData()) {
            String nutrient = data.getXValue();
            switch (nutrient) {
                case "Nitrogen":
                    data.getNode().setStyle("-fx-bar-fill: #43cea2;");
                    break;
                case "Phosphorus":
                    data.getNode().setStyle("-fx-bar-fill: #185a9d;");
                    break;
                case "Potassium":
                    data.getNode().setStyle("-fx-bar-fill: #ff6b6b;");
                    break;
            }
        }
    }
    
    @FXML
    private void copyRecommendations() {
        String recommendations = recommendationsArea.getText();
        if (recommendations != null && !recommendations.isEmpty()) {
            ClipboardContent content = new ClipboardContent();
            content.putString(recommendations);
            Clipboard.getSystemClipboard().setContent(content);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Copy Successful");
            alert.setHeaderText("Copy Successful");
            alert.setContentText("Recommendations have been copied to clipboard.");
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            dialogPane.getStyleClass().add("dialog-pane");
            
            alert.showAndWait();
        }
    }
} 