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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/" + fxmlFile)
            );

            Parent newPage = loader.load();

            rootPane.getChildren().setAll(newPage);


            AnchorPane.setTopAnchor(newPage, 0.0);
            AnchorPane.setBottomAnchor(newPage, 0.0);
            AnchorPane.setLeftAnchor(newPage, 0.0);
            AnchorPane.setRightAnchor(newPage, 0.0);

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Unable to open page");
            alert.setContentText(
                    "Please check the FXML file name and location:\n"
                            + fxmlFile
            );
            alert.showAndWait();
        }
    }


    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        loadPage("Dashboard.fxml");
    }

    @FXML
    void btnAddBookOnAction(ActionEvent event) {
        loadPage("AddBook.fxml");
    }

    @FXML
    void btnAddMemberOnAction(ActionEvent event) {
        loadPage("AddMember.fxml");
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        loadPage("IssueBook.fxml");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("ReturnBook.fxml");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("ManageMembers.fxml");
    }

    @FXML
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        loadPage("BorrowingHistory.fxml");
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Click OK to return to the login page.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                loadPage("Login.fxml");
            }
        });
    }
}