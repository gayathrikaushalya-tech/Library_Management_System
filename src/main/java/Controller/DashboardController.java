package Controller;

import Util.NavigatableController;
import Util.PageNavigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

public class DashboardController implements NavigatableController {

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnManageMembers;
    @FXML private Button btnReturnBook;
    @FXML private AnchorPane rootPane;

    private PageNavigator navigator;

    @FXML
    public void initialize() {

        if (navigator == null) {
            navigator = new PageNavigator(rootPane);
        }
    }

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
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
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        navigateTo("borrowing_history");
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Return to the login page?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {
            navigateTo("login_page");
        }
    }

    private void navigateTo(String pageName) {
        if (navigator == null) {
            showNavigationError();
            return;
        }

        navigator.navigate(pageName);
    }

    private void showNavigationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText(null);
        alert.setContentText("Page navigation has not been initialized.");
        alert.showAndWait();
    }
}