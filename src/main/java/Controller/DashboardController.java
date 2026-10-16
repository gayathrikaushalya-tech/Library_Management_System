package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button btnAddBook;

    @FXML
    private Button btnAddMember;

    @FXML
    private Button btnBorrowingHistory;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnIssueBook;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnManageMembers;

    @FXML
    private Button btnReturnBook;

    @FXML
    private AnchorPane rootPane;

    private void loadPage(String fxmlFile) {
        try {
            java.net.URL resource = getClass()
                    .getResource("/View/" + fxmlFile);

            if (resource == null) {
                System.out.println("FXML file not found: " + fxmlFile);
                return;
            }

            Parent page = FXMLLoader.load(resource);

            rootPane.getChildren().setAll(page);

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

        } catch (Exception e) {
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
    void btnIssueBookOnAction(ActionEvent event) {
        loadPage("issue_book.fxml");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("return_book.fxml");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("manage_members.fxml");
    }

    @FXML
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        loadPage("borrowing_history.fxml");
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Click OK to return to the login page.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                loadPage("login_page.fxml");
            }
        });
    }
}