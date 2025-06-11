package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;

public class DashboardController {
    @FXML private JFXDrawer drawer;
    @FXML private JFXButton menuButton;
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;
    
    private User currentUser;
    private boolean drawerOpen = false;

    @FXML
    private void initialize() {
        // Initialize drawer
        drawer.setDefaultDrawerSize(250);
        drawer.setOverLayVisible(true);
        drawer.setResizeContent(true);
        
        // Set initial content to dashboard
        showDashboard();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName());
    }

    @FXML
    private void toggleDrawer() {
        drawerOpen = !drawerOpen;
        if (drawerOpen) {
            drawer.open();
        } else {
            drawer.close();
        }
    }

    @FXML
    private void showDashboard() {
        loadContent("/fxml/DashboardHome.fxml");
        drawer.close();
        drawerOpen = false;
    }

    @FXML
    private void showNewAnalysis() {
        loadContent("/fxml/NewAnalysis.fxml");
        drawer.close();
        drawerOpen = false;
    }

    @FXML
    private void showHistory() {
        loadContent("/fxml/AnalysisHistory.fxml");
        drawer.close();
        drawerOpen = false;
    }

    @FXML
    private void showRecommendations() {
        loadContent("/fxml/Recommendations.fxml");
        drawer.close();
        drawerOpen = false;
    }

    @FXML
    private void showProfile() {
        loadContent("/fxml/Profile.fxml");
        drawer.close();
        drawerOpen = false;
    }

    @FXML
    private void handleLogout() {
        try {
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) menuButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            
            // If the loaded controller needs the current user, set it
            if (loader.getController() instanceof UserAwareController) {
                ((UserAwareController) loader.getController()).setCurrentUser(currentUser);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 