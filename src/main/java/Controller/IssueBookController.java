package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

public class IssueBookController {

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
    private Button btnIssue;

    @FXML
    private Button btnIssueBook;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnLogoutIcon;

    @FXML
    private Button btnManageMembers;

    @FXML
    private Button btnReturnBook;

    @FXML
    private Button btnUserIcon;

    @FXML
    private ComboBox<String> cmbBook;

    @FXML
    private ComboBox<String> cmbMember;

    @FXML
    private DatePicker dpDueDate;

    @FXML
    private DatePicker dpIssueDate;

    @FXML
    private AnchorPane rootPane;


    @FXML
    public void initialize() {


        cmbBook.getItems().setAll(
                "B001 - Harry Potter",
                "B002 - The Jungle Book",
                "B003 - Introduction to Java",
                "B004 - Science for Beginners",
                "B005 - World History"
        );


        cmbMember.getItems().setAll(
                "M001 - Nimesha Perera",
                "M002 - Kasun Silva",
                "M003 - Dulani Fernando",
                "M004 - Sanduni Wijesekara",
                "M005 - Tharindu Jayasinghe"
        );

        dpIssueDate.setValue(LocalDate.now());


        dpDueDate.setValue(LocalDate.now().plusDays(14));


        dpDueDate.setDayCellFactory(datePicker ->
                new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(
                            LocalDate date, boolean empty) {

                        super.updateItem(date, empty);

                        LocalDate issueDate = dpIssueDate.getValue();

                        if (empty || issueDate == null) {
                            setDisable(empty);
                        } else {
                            setDisable(date.isBefore(issueDate));
                        }
                    }
                }
        );
    }


    @FXML
    void btnIssueOnAction(ActionEvent event) {

        String book = cmbBook.getValue();
        String member = cmbMember.getValue();

        LocalDate issueDate = dpIssueDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();


        if (book == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Book",
                    "Please select a book."
            );
            return;
        }


        if (member == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Member",
                    "Please select a member."
            );
            return;
        }


        if (issueDate == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Issue Date",
                    "Please select the issue date."
            );
            return;
        }


        if (dueDate == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Due Date",
                    "Please select the due date."
            );
            return;
        }


        if (dueDate.isBefore(issueDate)) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Due Date",
                    "The due date cannot be before the issue date."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Issue Book");
        confirmation.setHeaderText("Confirm Book Issue");
        confirmation.setContentText(
                "Book: " + book
                        + "\nMember: " + member
                        + "\nIssue Date: " + issueDate
                        + "\nDue Date: " + dueDate
                        + "\n\nDo you want to issue this book?"
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Book issued successfully!\n\n"
                            + "Book: " + book
                            + "\nMember: " + member
                            + "\nIssue Date: " + issueDate
                            + "\nDue Date: " + dueDate
            );

            clearFields();
        }
    }


    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {

        cmbBook.getSelectionModel().clearSelection();
        cmbMember.getSelectionModel().clearSelection();

        dpIssueDate.setValue(LocalDate.now());
        dpDueDate.setValue(LocalDate.now().plusDays(14));
    }


    @FXML
    void cmbBookOnAction(ActionEvent event) {

    }


    @FXML
    void cmbMemberOnAction(ActionEvent event) {

    }


    @FXML
    void dpIssueDateOnAction(ActionEvent event) {

        LocalDate issueDate = dpIssueDate.getValue();

        if (issueDate == null) {
            return;
        }


        if (dpDueDate.getValue() == null
                || dpDueDate.getValue().isBefore(issueDate)) {

            dpDueDate.setValue(issueDate.plusDays(14));
        }
    }


    @FXML
    void dpDueDateOnAction(ActionEvent event) {

        LocalDate issueDate = dpIssueDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();

        if (issueDate != null && dueDate != null
                && dueDate.isBefore(issueDate)) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Due Date",
                    "Please select a due date on or after "
                            + "the issue date."
            );

            dpDueDate.setValue(issueDate.plusDays(14));
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


    private void loadPage(String fxmlFile) {

        try {
            URL resource = getClass().getResource(
                    "/View/" + fxmlFile
            );

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
    void btnBorrowingHistoryOnACtion(ActionEvent event) {
        loadPage("borrowing_history.fxml");
    }


    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("manage_members.fxml");
    }


    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("return_book.fxml");
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

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION
        );

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

    public void btnIssueBookOnAction(ActionEvent actionEvent) {
        loadPage("issue_book.fxml");
    }
}
