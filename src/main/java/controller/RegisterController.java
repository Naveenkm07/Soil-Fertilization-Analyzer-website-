package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;
import util.DatabaseUtil;

public class RegisterController {
    @FXML private JFXTextField fullNameField;
    @FXML private JFXTextField emailField;
    @FXML private JFXTextField usernameField;
    @FXML private JFXPasswordField passwordField;
    @FXML private JFXPasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private JFXButton registerButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        // Add event handlers
        registerButton.setOnAction(this::handleRegister);
        loginLink.setOnAction(this::handleLogin);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Validate input
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // Clear previous error
        errorLabel.setText("");

        // Validate all fields
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("Please enter a valid email address.");
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        // Validate password strength
        if (password.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters long.");
            return;
        }

        // Create user and register
        User newUser = new User(0, username, password, fullName, email, role);
        if (DatabaseUtil.registerUser(newUser)) {
            try {
                // Registration successful, switch to login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) registerButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                errorLabel.setText("Error loading login screen: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Registration failed. Username or email may already exist.");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            // Switch to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Error loading login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 