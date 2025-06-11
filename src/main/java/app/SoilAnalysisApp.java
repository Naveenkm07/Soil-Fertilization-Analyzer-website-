package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import com.soilanalysis.model.SoilAnalysis;
import model.User;
import util.DatabaseUtil;
import util.WeatherService;

public class SoilAnalysisApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        
        // Create scene with modern styling
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        // Configure primary stage
        primaryStage.setTitle("Soil Analysis System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        
        // Add modern window effects
        primaryStage.setOpacity(0);
        primaryStage.show();
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @Override
    public void stop() {
        // No need to close database connection anymore
    }

    public static void main(String[] args) {
        launch(args);
    }
} 