package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginPageController {

    @FXML private Button btnClear;
    @FXML private Button btnLogin;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtUsername;

    @FXML
    void btnClearOnAction(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        txtUsername.requestFocus();
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please enter both username and password.");
            return;
        }


        if (username.equals("admin")
                && password.equals("admin123")) {

            try {
                URL resource =
                        getClass().getResource("/View/dashboard.fxml");

                if (resource == null) {
                    showAlert(Alert.AlertType.ERROR,
                            "Navigation Error",
                            "Cannot find /View/dashboard.fxml.");
                    return;
                }

                Parent root = FXMLLoader.load(resource);

                Stage stage =
                        (Stage) btnLogin.getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("Library Management System");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();

                showAlert(Alert.AlertType.ERROR,
                        "Loading Error",
                        "Unable to open the dashboard.\n"
                                + e.getMessage());
            }

        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Login Failed",
                    "Invalid username or password.");

            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}