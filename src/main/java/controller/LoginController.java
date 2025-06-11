package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;
import util.DatabaseUtil;

public class LoginController {
    @FXML private JFXTextField usernameField;
    @FXML private JFXPasswordField passwordField;
    @FXML private JFXButton loginButton;
    @FXML private Hyperlink registerLink;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Database is now initialized automatically in DatabaseUtil's static block
        errorLabel.setVisible(false);
        
        // Add hover effects
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2E7D32;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #4CAF50;"));
        
        registerLink.setOnMouseEntered(e -> registerLink.setStyle("-fx-background-color: #1565C0;"));
        registerLink.setOnMouseExited(e -> registerLink.setStyle("-fx-background-color: #2196F3;"));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        User user = DatabaseUtil.authenticateUser(username, password);
        if (user != null) {
            try {
                // Load dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                Parent root = loader.load();
                
                // Get the current stage
                Stage stage = (Stage) loginButton.getScene().getWindow();
                
                // Set the new scene
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                errorLabel.setText("Error loading dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Load registration screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) registerLink.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error loading registration screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 