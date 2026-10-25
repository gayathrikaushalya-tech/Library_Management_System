package Controller;

import DAO.MemberDAO;
import Model.Member;
import Util.NavigatableController;
import Util.PageNavigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Optional;

public class AddMemberController implements NavigatableController {

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnClear;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnLogoutIcon;
    @FXML private Button btnManageMembers;
    @FXML private Button btnRegister;
    @FXML private Button btnReturnBook;
    @FXML private Button btnUserIcon;

    @FXML private TextArea txtAddress;
    @FXML private TextField txtEmail;
    @FXML private TextField txtMemberID;
    @FXML private TextField txtName;
    @FXML private TextField txtPhoneNumber;

    private final MemberDAO memberDAO = new MemberDAO();
    private PageNavigator navigator;

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        // The database generates the member ID.
        txtMemberID.setEditable(false);
        txtMemberID.setPromptText("Auto-generated");
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNumber.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || email.isEmpty()
                || phone.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all member details.");
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid Email",
                    "Please enter a valid email address.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid Phone Number",
                    "Please enter a 10-digit phone number.");
            return;
        }

        Member member = new Member(name, email, phone, address);

        try {
            if (memberDAO.addMember(member)) {
                txtMemberID.setText(member.getMemberId());

                showAlert(Alert.AlertType.INFORMATION,
                        "Registration Successful",
                        "Member registered successfully.\n\n"
                                + "Member ID: " + member.getMemberId()
                                + "\nName: " + member.getFullName());

                clearForm();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to register member. Please check "
                            + "your database connection and table columns.\n"
                            + e.getMessage());
        }
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
        txtName.requestFocus();
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        navigateTo("dashboard");
    }

    @FXML
    void btnAddBookOnAction(ActionEvent event) {
        navigateTo("add_book_page");
    }

    @FXML
    void btnAddMemberOnAction(ActionEvent event) {
        navigateTo("add_member");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        navigateTo("manage_members");
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        navigateTo("issue_book");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        navigateTo("return_book");
    }

    @FXML
    void btnBorrowingHistoryOnACtion(ActionEvent event) {
        navigateTo("borrowing_history");
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
        alert.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            navigateTo("login_page");
        }
    }

    @FXML
    void btnUserIconOnAction(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION,
                "User Information",
                "Logged-in user: Admin");
    }

    private void navigateTo(String pageName) {
        if (navigator == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Navigation Error",
                    "The shared PageNavigator has not been initialized.");
            return;
        }

        navigator.navigate(pageName);
    }

    private void showAlert(Alert.AlertType type,
                           String title,
                           String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}