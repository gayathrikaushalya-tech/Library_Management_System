package Controller;

import DAO.BorrowingDAO;
import Model.Borrowing;
import Util.NavigatableController;
import Util.PageNavigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ReturnBookController implements NavigatableController {

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnLogoutIcon;
    @FXML private Button btnManageMembers;
    @FXML private Button btnReturnBook;
    @FXML private Button btnReturn;
    @FXML private Button btnSearch;
    @FXML private Button btnUserIcon;

    @FXML private DatePicker dpReturnDate;

    @FXML private Label lblBorrowBook;
    @FXML private Label lblBorrowDate;
    @FXML private Label lblDueDate;
    @FXML private Label lblLate;
    @FXML private Label lblMemberID;
    @FXML private Label lblName;
    @FXML private Label lblPhone;

    @FXML private AnchorPane rootPane;
    @FXML private TextField txtEnterBorrowID;

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    private Borrowing currentRecord;
    private PageNavigator navigator;

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        dpReturnDate.setValue(LocalDate.now());

        dpReturnDate.valueProperty().addListener(
                (observable, oldDate, newDate) -> updateLateDays()
        );

        clearDetails();
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String borrowingCode = txtEnterBorrowID.getText() == null
                ? ""
                : txtEnterBorrowID.getText().trim().toUpperCase();

        if (borrowingCode.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Borrow ID",
                    "Please enter a borrowing ID, for example BR0001."
            );
            return;
        }

        try {
            currentRecord = findBorrowing(borrowingCode);

            if (currentRecord == null) {
                clearDetails();

                showAlert(
                        Alert.AlertType.WARNING,
                        "Record Not Found",
                        "No borrowing record was found for "
                                + borrowingCode + "."
                );
                return;
            }

            displayRecord(currentRecord);

            if (isReturned(currentRecord)) {
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Already Returned",
                        "This book has already been returned."
                );
            }

        } catch (SQLException e) {
            currentRecord = null;
            clearDetails();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to search borrowing records.\n"
                            + e.getMessage()
            );
        }
    }

    private Borrowing findBorrowing(String borrowingCode)
            throws SQLException {

        for (Borrowing borrowing : borrowingDAO.getAllBorrowings()) {
            if (borrowing.getBorrowingCode() != null
                    && borrowing.getBorrowingCode()
                    .equalsIgnoreCase(borrowingCode)) {
                return borrowing;
            }
        }

        return null;
    }

    private void displayRecord(Borrowing record) {
        lblBorrowBook.setText(textOrDash(record.getBookTitle()));

        lblMemberID.setText(getMemberCode(record));
        lblName.setText(textOrDash(record.getMemberName()));
        lblPhone.setText(getMemberPhone(record));

        lblBorrowDate.setText(dateOrDash(record.getIssueDate()));
        lblDueDate.setText(dateOrDash(record.getDueDate()));

        updateLateDays();
    }

    private void updateLateDays() {
        if (currentRecord == null
                || dpReturnDate.getValue() == null
                || currentRecord.getDueDate() == null) {
            lblLate.setText("-");
            return;
        }

        if (isReturned(currentRecord)) {
            lblLate.setText("Already returned");
            return;
        }

        LocalDate returnDate = dpReturnDate.getValue();

        long lateDays = Math.max(
                0,
                ChronoUnit.DAYS.between(
                        currentRecord.getDueDate(),
                        returnDate
                )
        );

        lblLate.setText(
                lateDays == 0
                        ? "Not overdue"
                        : lateDays + " day(s) overdue"
        );
    }

    @FXML
    void btnReturnOnAction(ActionEvent event) {
        if (currentRecord == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Record Selected",
                    "Search for a valid borrowing record first."
            );
            return;
        }

        if (isReturned(currentRecord)) {
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Already Returned",
                    "This book has already been returned."
            );
            return;
        }

        LocalDate returnDate = dpReturnDate.getValue();

        if (returnDate == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Return Date",
                    "Please select a return date."
            );
            return;
        }

        if (currentRecord.getIssueDate() != null
                && returnDate.isBefore(currentRecord.getIssueDate())) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Return Date",
                    "Return date cannot be before the issue date."
            );
            return;
        }

        if (returnDate.isAfter(LocalDate.now())) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Return Date",
                    "Return date cannot be in the future."
            );
            return;
        }

        long lateDays = 0;

        if (currentRecord.getDueDate() != null) {
            lateDays = Math.max(
                    0,
                    ChronoUnit.DAYS.between(
                            currentRecord.getDueDate(),
                            returnDate
                    )
            );
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Confirm Return");
        confirmation.setHeaderText("Confirm Book Return");
        confirmation.setContentText(
                "Book: " + currentRecord.getBookTitle()
                        + "\nBorrow ID: "
                        + currentRecord.getBorrowingCode()
                        + "\nMember: " + currentRecord.getMemberName()
                        + "\nReturn Date: " + returnDate
                        + "\nOverdue Days: " + lateDays
                        + "\n\nConfirm this return?"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (!result.isPresent()
                || result.get() != ButtonType.OK) {
            return;
        }

        try {

            boolean success = borrowingDAO.returnBook(
                    currentRecord.getBorrowingCode()
            );

            if (success) {
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Return Successful",
                        "Book return processed successfully.\n\n"
                                + "Book: " + currentRecord.getBookTitle()
                                + "\nMember: "
                                + currentRecord.getMemberName()
                                + "\nOverdue Days: " + lateDays
                );

                currentRecord = null;
                txtEnterBorrowID.clear();
                dpReturnDate.setValue(LocalDate.now());
                clearDetails();

            } else {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Return Not Processed",
                        "The borrowing record could not be returned."
                );
            }

        } catch (SQLException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Return Failed",
                    "Unable to process the book return.\n"
                            + e.getMessage()
            );
        }
    }

    private boolean isReturned(Borrowing record) {
        return record.getStatus() != null
                && record.getStatus().equalsIgnoreCase("Returned");
    }

    private String textOrDash(String value) {
        return value == null || value.trim().isEmpty()
                ? "-" : value;
    }

    private String dateOrDash(LocalDate date) {
        return date == null ? "-" : date.toString();
    }

    private String getMemberCode(Borrowing record) {
        return "-";
    }

    private String getMemberPhone(Borrowing record) {
        return "-";
    }

    private void clearDetails() {
        lblBorrowBook.setText("-");
        lblMemberID.setText("-");
        lblName.setText("-");
        lblPhone.setText("-");
        lblBorrowDate.setText("-");
        lblDueDate.setText("-");
        lblLate.setText("-");
    }

    private void navigate(String pageName) {
        if (navigator == null) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "PageNavigator was not injected into this controller."
            );
            return;
        }

        navigator.navigate(pageName);
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        navigate("dashboard");
    }

    @FXML
    void btnAddBookOnAction(ActionEvent event) {
        navigate("add_book_page");
    }

    @FXML
    void btnAddMemberOnAction(ActionEvent event) {
        navigate("add_member");
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        navigate("issue_book");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        navigate("manage_members");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        navigate("return_book");
    }

    @FXML
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        navigate("borrowing_history");
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

        if (!result.isPresent()
                || result.get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/login_page.fxml")
            );

            Parent loginPage = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();

            stage.setScene(new Scene(loginPage));
            stage.show();

        } catch (IOException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Logout Error",
                    "Unable to load the login page.\n"
                            + e.getMessage()
            );
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