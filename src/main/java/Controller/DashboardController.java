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
import Util.DBConnection;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    @FXML private Label lblTotalBooks;
    @FXML private Label lblTotalMembers;
    @FXML private Label lblBorrowedBooks;
    @FXML private Label lblOverdueBooks;

    private PageNavigator navigator;

    @FXML
    public void initialize() {

        if (navigator == null) {
            navigator = new PageNavigator(rootPane);
        }

        loadDashboardCounts();
    }

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }
    private void loadDashboardCounts() {

        String totalBooksSQL =
                "SELECT COALESCE(SUM(quantity), 0) FROM books";

        String totalMembersSQL =
                "SELECT COUNT(*) FROM members";

        String borrowedBooksSQL =
                "SELECT COUNT(*) FROM borrowings "
                        + "WHERE status IS NULL OR status <> 'Returned'";

        String overdueBooksSQL =
                "SELECT COUNT(*) FROM borrowings "
                        + "WHERE (status IS NULL OR status <> 'Returned') "
                        + "AND due_date < CURDATE()";

        try (Connection connection = DBConnection.getConnection()) {

            lblTotalBooks.setText(
                    getCount(connection, totalBooksSQL));

            lblTotalMembers.setText(
                    getCount(connection, totalMembersSQL));

            lblBorrowedBooks.setText(
                    getCount(connection, borrowedBooksSQL));

            lblOverdueBooks.setText(
                    getCount(connection, overdueBooksSQL));

        } catch (SQLException e) {
            e.printStackTrace();

            showDashboardError(
                    "Unable to load dashboard statistics.\n"
                            + e.getMessage());
        }
    }

    private String getCount(
            Connection connection,
            String sql) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return String.valueOf(resultSet.getInt(1));
            }

            return "0";
        }
    }

    private void showDashboardError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dashboard Database Error");
        alert.setHeaderText("Unable to load statistics");
        alert.setContentText(message);
        alert.showAndWait();
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