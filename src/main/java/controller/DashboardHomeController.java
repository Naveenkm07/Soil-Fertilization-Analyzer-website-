package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import com.jfoenix.controls.*;
import model.User;
import model.SoilAnalysis;
import util.DatabaseUtil;
import util.WeatherService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardHomeController implements UserAwareController {
    @FXML private Label recentAnalysesCount;
    @FXML private Label activeRecommendationsCount;
    @FXML private Label soilHealthScore;
    @FXML private Label lastUpdatedLabel;
    @FXML private TableView<SoilAnalysis> recentActivityTable;
    @FXML private TableColumn<SoilAnalysis, String> dateColumn;
    @FXML private TableColumn<SoilAnalysis, String> typeColumn;
    @FXML private TableColumn<SoilAnalysis, String> descriptionColumn;
    @FXML private TableColumn<SoilAnalysis, String> statusColumn;
    @FXML private ProgressBar analysesProgress;
    @FXML private ProgressBar recommendationsProgress;
    @FXML private ProgressBar healthProgress;
    @FXML private LineChart<String, Number> healthTrendChart;
    @FXML private JFXTextField searchField;
    @FXML private JFXButton notificationButton;
    @FXML private ImageView weatherIcon;
    @FXML private Label temperatureLabel;
    @FXML private Label weatherDescription;
    @FXML private Label soilMoistureLabel;
    @FXML private JFXComboBox<String> activityFilter;
    @FXML private JFXComboBox<String> chartTimeRange;
    @FXML private JFXButton exportChartButton;
    @FXML private VBox notificationCenter;
    @FXML private ListView<Notification> notificationList;

    private User currentUser;
    private WeatherService weatherService;
    private List<Notification> notifications;
    private Timer weatherUpdateTimer;
    private static final Duration CARD_HOVER_DURATION = Duration.millis(200);

    @FXML
    private void initialize() {
        // Initialize table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Format date column
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                }
            }
        });

        // Add status styling
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "pending":
                            setStyle("-fx-background-color: #ffd700; -fx-text-fill: #856404;");
                            break;
                        case "completed":
                            setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                            break;
                        case "error":
                            setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                            break;
                    }
                }
            }
        });

        // Initialize chart
        healthTrendChart.setTitle("Soil Health Trend");
        healthTrendChart.setCreateSymbols(true);
        healthTrendChart.setAnimated(true);

        // Initialize new components
        initializeSearch();
        initializeWeatherWidget();
        initializeFilters();
        initializeNotifications();
        setupCardHoverEffects();
    }

    private void initializeSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                List<SoilAnalysis> filteredAnalyses = DatabaseUtil.searchAnalyses(currentUser.getId(), newVal);
                recentActivityTable.getItems().setAll(filteredAnalyses);
            } else if (newVal.isEmpty()) {
                refreshDashboard();
            }
        });
    }

    private void initializeWeatherWidget() {
        weatherService = new WeatherService();
        updateWeather();
        
        // Update weather every 30 minutes
        weatherUpdateTimer = new Timer(true);
        weatherUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateWeather();
            }
        }, 0, 30 * 60 * 1000);
    }

    private void updateWeather() {
        WeatherService.WeatherData weather = weatherService.getCurrentWeather();
        javafx.application.Platform.runLater(() -> {
            temperatureLabel.setText(String.format("%.1f°C", weather.getTemperature()));
            weatherDescription.setText(weather.getDescription());
            soilMoistureLabel.setText(String.format("Soil Moisture: %.1f%%", weather.getSoilMoisture()));
            
            // Update weather icon
            String iconPath = "/images/weather/" + weather.getIconCode() + ".png";
            try {
                weatherIcon.setImage(new Image(getClass().getResourceAsStream(iconPath)));
            } catch (Exception e) {
                weatherIcon.setImage(new Image(getClass().getResourceAsStream("/images/weather/default.png")));
            }
        });
    }

    private void initializeFilters() {
        // Initialize activity filter
        activityFilter.getItems().addAll("All", "Pending", "Completed", "Error");
        activityFilter.setValue("All");
        activityFilter.setOnAction(e -> filterActivities());

        // Initialize chart time range
        chartTimeRange.getItems().addAll("Last 7 days", "Last 30 days", "Last 90 days", "Last year");
        chartTimeRange.setValue("Last 30 days");
        chartTimeRange.setOnAction(e -> updateHealthTrendChart(DatabaseUtil.getRecentAnalyses(currentUser.getId())));
    }

    private void initializeNotifications() {
        notifications = new ArrayList<>();
        notificationList.setCellFactory(lv -> new NotificationCell());
        
        // Add some sample notifications
        addNotification("System", "Welcome to Soil Fertility Oracle!", "info");
        addNotification("Analysis", "New soil analysis results available", "success");
    }

    private void setupCardHoverEffects() {
        // Add hover effects to all dashboard cards
        for (VBox card : Arrays.asList(
            (VBox) recentAnalysesCount.getParent().getParent(),
            (VBox) activeRecommendationsCount.getParent().getParent(),
            (VBox) soilHealthScore.getParent().getParent()
        )) {
            card.setOnMouseEntered(e -> handleCardHover(card));
            card.setOnMouseExited(e -> handleCardExit(card));
        }
    }

    @FXML
    private void handleCardHover(VBox card) {
        ScaleTransition st = new ScaleTransition(CARD_HOVER_DURATION, card);
        st.setToX(1.05);
        st.setToY(1.05);
        st.play();

        DropShadow shadow = new DropShadow(10, Color.rgb(0, 0, 0, 0.2));
        card.setEffect(shadow);
    }

    @FXML
    private void handleCardExit(VBox card) {
        ScaleTransition st = new ScaleTransition(CARD_HOVER_DURATION, card);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();

        card.setEffect(null);
    }

    @FXML
    private void showNotifications() {
        boolean isVisible = notificationCenter.isVisible();
        notificationCenter.setVisible(!isVisible);
        notificationCenter.setManaged(!isVisible);
        
        if (!isVisible) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), notificationCenter);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    private void addNotification(String title, String message, String type) {
        Notification notification = new Notification(title, message, type, LocalDate.now());
        notifications.add(0, notification);
        notificationList.getItems().setAll(notifications);
        
        // Show notification badge
        notificationButton.setStyle("-fx-background-color: #ff4444;");
    }

    private void filterActivities() {
        String filter = activityFilter.getValue();
        List<SoilAnalysis> filteredAnalyses = DatabaseUtil.getRecentAnalyses(currentUser.getId());
        
        if (!filter.equals("All")) {
            filteredAnalyses = filteredAnalyses.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase(filter))
                .collect(Collectors.toList());
        }
        
        recentActivityTable.getItems().setAll(filteredAnalyses);
    }

    @FXML
    private void exportChart() {
        // Implementation for exporting chart as image
        // ... existing code ...
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshDashboard();
    }

    private void refreshDashboard() {
        if (currentUser == null) return;

        // Update last updated time
        lastUpdatedLabel.setText("Last updated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

        // Load data from database
        int analysesCount = DatabaseUtil.getAnalysisCount(currentUser.getId());
        int recommendationsCount = DatabaseUtil.getActiveRecommendationsCount(currentUser.getId());
        double healthScore = DatabaseUtil.getAverageSoilHealth(currentUser.getId());

        // Update counts with animations
        animateValue(recentAnalysesCount, analysesCount);
        animateValue(activeRecommendationsCount, recommendationsCount);
        animateValue(soilHealthScore, (int)healthScore, "%");

        // Update progress bars
        analysesProgress.setProgress(analysesCount / 20.0); // Assuming 20 is max for the month
        recommendationsProgress.setProgress(recommendationsCount / 10.0); // Assuming 10 is max
        healthProgress.setProgress(healthScore / 100.0);

        // Load recent activity
        List<SoilAnalysis> recentAnalyses = DatabaseUtil.getRecentAnalyses(currentUser.getId());
        recentActivityTable.getItems().setAll(recentAnalyses);

        // Update health trend chart
        updateHealthTrendChart(recentAnalyses);
    }

    private void animateValue(Label label, int targetValue) {
        animateValue(label, targetValue, "");
    }

    private void animateValue(Label label, int targetValue, String suffix) {
        final int startValue = 0;
        final int duration = 1000; // 1 second
        final int steps = 20;
        final int stepDuration = duration / steps;
        final int increment = targetValue / steps;

        new Thread(() -> {
            for (int i = 0; i <= steps; i++) {
                final int currentValue = startValue + (increment * i);
                javafx.application.Platform.runLater(() -> 
                    label.setText(currentValue + suffix));
                try {
                    Thread.sleep(stepDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateHealthTrendChart(List<SoilAnalysis> analyses) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Soil Health Score");

        // Sort analyses by date
        analyses.sort((a1, a2) -> a1.getDate().compareTo(a2.getDate()));

        // Add data points
        for (SoilAnalysis analysis : analyses) {
            double healthScore = calculateHealthScore(analysis);
            series.getData().add(new XYChart.Data<>(
                LocalDate.parse(analysis.getDate()).format(DateTimeFormatter.ofPattern("MMM dd")),
                healthScore
            ));
        }

        // Update chart with animation
        healthTrendChart.getData().clear();
        healthTrendChart.getData().add(series);

        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), healthTrendChart);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private double calculateHealthScore(SoilAnalysis analysis) {
        // Calculate a weighted average of soil parameters
        double phScore = calculatePhScore(analysis.getPh());
        double nutrientScore = calculateNutrientScore(
            analysis.getNitrogen(),
            analysis.getPhosphorus(),
            analysis.getPotassium()
        );
        double organicScore = analysis.getOrganicMatter() / 10.0; // Assuming 10% is optimal
        double moistureScore = analysis.getMoisture() / 100.0;

        return (phScore * 0.3 + nutrientScore * 0.4 + organicScore * 0.2 + moistureScore * 0.1) * 100;
    }

    private double calculatePhScore(double ph) {
        // Optimal pH is between 6.0 and 7.0
        if (ph >= 6.0 && ph <= 7.0) return 1.0;
        if (ph >= 5.5 && ph <= 7.5) return 0.8;
        if (ph >= 5.0 && ph <= 8.0) return 0.6;
        return 0.4;
    }

    private double calculateNutrientScore(double nitrogen, double phosphorus, double potassium) {
        // Optimal ranges: N: 40-200, P: 20-100, K: 150-300 mg/kg
        double nScore = Math.min(nitrogen / 200.0, 1.0);
        double pScore = Math.min(phosphorus / 100.0, 1.0);
        double kScore = Math.min(potassium / 300.0, 1.0);
        return (nScore + pScore + kScore) / 3.0;
    }

    @FXML
    private void startNewAnalysis() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewAnalysis.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) recentActivityTable.getScene().getWindow();
            
            // Set the new scene with fade transition
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), recentActivityTable.getScene().getRoot());
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
    private void viewHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnalysisHistory.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) recentActivityTable.getScene().getWindow();
            
            // Set the new scene with fade transition
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), recentActivityTable.getScene().getRoot());
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
    private void generateReport() {
        // TODO: Implement report generation with modern UI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Generation");
        alert.setHeaderText("Report Generation");
        alert.setContentText("Report generation feature will be implemented soon.");
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        
        alert.showAndWait();
    }

    // Inner class for notifications
    private static class Notification {
        private final String title;
        private final String message;
        private final String type;
        private final LocalDate date;

        public Notification(String title, String message, String type, LocalDate date) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.date = date;
        }

        // Getters
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getType() { return type; }
        public LocalDate getDate() { return date; }
    }

    // Custom cell for notifications
    private static class NotificationCell extends ListCell<Notification> {
        @Override
        protected void updateItem(Notification notification, boolean empty) {
            super.updateItem(notification, empty);
            
            if (empty || notification == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox content = new VBox(5);
                HBox header = new HBox(10);
                
                Label titleLabel = new Label(notification.getTitle());
                titleLabel.setStyle("-fx-font-weight: bold;");
                
                Label dateLabel = new Label(notification.getDate().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
                dateLabel.setStyle("-fx-text-fill: #666;");
                
                header.getChildren().addAll(titleLabel, dateLabel);
                
                Label messageLabel = new Label(notification.getMessage());
                messageLabel.setWrapText(true);
                
                content.getChildren().addAll(header, messageLabel);
                content.setStyle("-fx-padding: 10; -fx-background-color: " + getTypeColor(notification.getType()) + ";");
                
                setGraphic(content);
            }
        }

        private String getTypeColor(String type) {
            switch (type.toLowerCase()) {
                case "success": return "#e8f5e9";
                case "error": return "#ffebee";
                case "warning": return "#fff3e0";
                default: return "#f5f5f5";
            }
        }
    }
} 