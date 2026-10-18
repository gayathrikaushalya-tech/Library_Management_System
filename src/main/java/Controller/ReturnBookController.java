package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReturnBookController {

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnLogoutIcon;
    @FXML private Button btnManageMembers;
    @FXML private Button btnReturnBook;
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

    private static final Map<String, BorrowRecord> borrowRecords =
            new HashMap<>();

    private BorrowRecord currentRecord;

    private static class BorrowRecord {
        String borrowID;
        String bookTitle;
        String memberID;
        String memberName;
        String phone;
        LocalDate borrowDate;
        LocalDate dueDate;
        boolean returned;

        BorrowRecord(
                String borrowID,
                String bookTitle,
                String memberID,
                String memberName,
                String phone,
                LocalDate borrowDate,
                LocalDate dueDate) {

            this.borrowID = borrowID;
            this.bookTitle = bookTitle;
            this.memberID = memberID;
            this.memberName = memberName;
            this.phone = phone;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.returned = false;
        }
    }

    @FXML
    public void initialize() {


        if (borrowRecords.isEmpty()) {
            LocalDate today = LocalDate.now();

            borrowRecords.put(
                    "BR001",
                    new BorrowRecord(
                            "BR001",
                            "Harry Potter",
                            "M001",
                            "Nimesha Perera",
                            "0771234567",
                            today.minusDays(20),
                            today.minusDays(6)
                    )
            );

            borrowRecords.put(
                    "BR002",
                    new BorrowRecord(
                            "BR002",
                            "Introduction to Java",
                            "M002",
                            "Kasun Silva",
                            "0712345678",
                            today.minusDays(3),
                            today.plusDays(11)
                    )
            );
        }

        dpReturnDate.setValue(LocalDate.now());
        clearDetails();
    }


    @FXML
    void btnSearchOnAction(ActionEvent event) {

        String borrowID = txtEnterBorrowID.getText()
                .trim()
                .toUpperCase();

        if (borrowID.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Borrow ID",
                    "Please enter a Borrow ID."
            );
            return;
        }

        BorrowRecord record = borrowRecords.get(borrowID);

        if (record == null) {
            currentRecord = null;
            clearDetails();

            showAlert(
                    Alert.AlertType.WARNING,
                    "Record Not Found",
                    "No borrowing record was found for "
                            + borrowID + "."
            );
            return;
        }

        if (record.returned) {
            currentRecord = null;
            displayRecord(record);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Already Returned",
                    "This book has already been returned."
            );
            return;
        }

        currentRecord = record;
        displayRecord(record);
    }


    private void displayRecord(BorrowRecord record) {

        lblBorrowBook.setText(record.bookTitle);
        lblMemberID.setText(record.memberID);
        lblName.setText(record.memberName);
        lblPhone.setText(record.phone);
        lblBorrowDate.setText(record.borrowDate.toString());
        lblDueDate.setText(record.dueDate.toString());

        updateLateDays();
    }


    private void updateLateDays() {

        if (currentRecord == null || dpReturnDate.getValue() == null) {
            lblLate.setText("-");
            return;
        }

        LocalDate returnDate = dpReturnDate.getValue();

        long lateDays = Math.max(
                0,
                ChronoUnit.DAYS.between(
                        currentRecord.dueDate,
                        returnDate
                )
        );

        if (lateDays == 0) {
            lblLate.setText("Not overdue");
        } else {
            lblLate.setText(lateDays + " day(s) overdue");
        }
    }


    @FXML
    void btnReturnBookOnAction(ActionEvent event) {

        if (currentRecord == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Record Selected",
                    "Search for a valid borrowing record first."
            );
            return;
        }

        if (currentRecord.returned) {
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

        if (returnDate.isBefore(currentRecord.borrowDate)) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Return Date",
                    "Return date cannot be before the borrow date."
            );
            return;
        }

        long lateDays = Math.max(
                0,
                ChronoUnit.DAYS.between(
                        currentRecord.dueDate,
                        returnDate
                )
        );

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Confirm Return");
        confirmation.setHeaderText("Confirm Book Return");

        confirmation.setContentText(
                "Book: " + currentRecord.bookTitle
                        + "\nBorrow ID: " + currentRecord.borrowID
                        + "\nMember: " + currentRecord.memberName
                        + "\nReturn Date: " + returnDate
                        + "\nOverdue Days: " + lateDays
                        + "\n\nConfirm this return?"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            currentRecord.returned = true;

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Return Successful",
                    "Book return processed successfully.\n\n"
                            + "Book: " + currentRecord.bookTitle
                            + "\nMember: " + currentRecord.memberName
                            + "\nOverdue Days: " + lateDays
            );

            currentRecord = null;
            clearDetails();
            txtEnterBorrowID.clear();
        }
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
                        "FXML file not found: /View/" + fxmlFile
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
            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "Unable to load " + fxmlFile
            );
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
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("manage_members.fxml");
    }

    @FXML
    void btnBorrowingHistoryOnACtion(ActionEvent event) {
        loadPage("borrowing_history.fxml");
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
        alert.setContentText("Are you sure you want to log out?");

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
}

