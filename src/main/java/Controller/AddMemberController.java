package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AddMemberController {

    @FXML
    private Button btnAddBook;

    @FXML
    private Button btnAddMember;

    @FXML
    private Button btnBorrowingHistory;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnIssueBook;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnLogoutIcon;

    @FXML
    private Button btnManageMembers;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnReturnBook;

    @FXML
    private Button btnUserIcon;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextArea txtAddress;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtMemberID;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhoneNumber;

    private static final Set<String> registeredMemberIds =
            new HashSet<>();



    private void loadPage(String fxmlFile) {
        try {
            URL resource =
                    getClass().getResource("/View/" + fxmlFile);

            if (resource == null) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Navigation Error",
                        "FXML file not found: " + fxmlFile
                );
                return;
            }

            Parent page = FXMLLoader.load(resource);

            rootPane.getChildren().setAll(page);

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);

        } catch (IOException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "Unable to load " + fxmlFile
            );
            e.printStackTrace();
        }
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        loadPage("dashboard.fxml");
    }

    @FXML
    void btnAddBookOnAction(ActionEvent event) {
        loadPage("add_book_page.fxml");
    }

    @FXML
    void btnAddMemberOnAction(ActionEvent event) {
        loadPage("add_member.fxml");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("manage_members.fxml");
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        loadPage("issue_book.fxml");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("return_book.fxml");
    }

    @FXML
    void btnBorrowingHistoryOnACtion(ActionEvent event) {
        loadPage("borrowing_history.fxml");
    }



    @FXML
    void btnRegisterOnAction(ActionEvent event) {

        String memberId = txtMemberID.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNumber.getText().trim();
        String address = txtAddress.getText().trim();

        if (memberId.isEmpty()
                || name.isEmpty()
                || email.isEmpty()
                || phone.isEmpty()
                || address.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all member details."
            );
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Email",
                    "Please enter a valid email address."
            );
            return;
        }


        if (!phone.matches("\\d{10}")) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Phone Number",
                    "Please enter a 10-digit phone number."
            );
            return;
        }


        if (registeredMemberIds.contains(memberId)) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Duplicate Member ID",
                    "This member ID is already registered."
            );
            return;
        }

        registeredMemberIds.add(memberId);

        showAlert(
                Alert.AlertType.INFORMATION,
                "Registration Successful",
                "Member registered successfully!\n\n"
                        + "Member ID: " + memberId
                        + "\nName: " + name
        );

        clearForm();
    }



    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        txtMemberID.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhoneNumber.clear();
        txtAddress.clear();
        txtMemberID.requestFocus();
    }



    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        logout();
    }

    @FXML
    void btnLogoutIconOnAction(ActionEvent event) {
        logout();
    }

    private void logout() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText(
                "Are you sure you want to log out?"
        );

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            loadPage("login_page.fxml");
        }
    }



    @FXML
    void btnUserIconOnAction(ActionEvent event) {
        showAlert(
                Alert.AlertType.INFORMATION,
                "User Information",
                "Logged-in user: Admin"
        );
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

