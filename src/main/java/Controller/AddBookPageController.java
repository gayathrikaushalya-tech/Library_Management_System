package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AddBookPageController {

    @FXML private Button btnAdd;
    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnClear;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogin;
    @FXML private Button btnLogoutIcon;
    @FXML private Button btnManageMembers;
    @FXML private Button btnReturnBook;
    @FXML private Button btnUserIcon;

    @FXML private ComboBox<String> comboBox;
    @FXML private AnchorPane rootPane;

    @FXML private TextField txtAuthor;
    @FXML private TextField txtBookID;
    @FXML private TextField txtBookTitle;
    @FXML private TextField txtPublishedYear;
    @FXML private TextField txtQuantity;


    private static final Set<String> bookIDs = new HashSet<>();

    @FXML
    public void initialize() {
        comboBox.getItems().setAll(
                "Fiction",
                "Non-Fiction",
                "Science",
                "Technology",
                "History",
                "Education",
                "Other"
        );
    }


    @FXML
    void btnAddOnAction(ActionEvent event) {

        String bookID = txtBookID.getText().trim();
        String title = txtBookTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String yearText = txtPublishedYear.getText().trim();
        String quantityText = txtQuantity.getText().trim();
        String category = comboBox.getValue();


        if (bookID.isEmpty()
                || title.isEmpty()
                || author.isEmpty()
                || yearText.isEmpty()
                || quantityText.isEmpty()
                || category == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all fields and select a category."
            );
            return;
        }

        int year;

        try {
            year = Integer.parseInt(yearText);

            if (year < 1 || year > java.time.Year.now().getValue()) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Year",
                        "Enter a valid published year."
                );
                return;
            }

        } catch (NumberFormatException e) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Year",
                    "Published year must contain numbers only."
            );
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);

            if (quantity <= 0) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Quantity",
                        "Quantity must be greater than zero."
                );
                return;
            }

        } catch (NumberFormatException e) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Quantity",
                    "Quantity must contain numbers only."
            );
            return;
        }


        String normalizedID = bookID.toLowerCase();

        if (bookIDs.contains(normalizedID)) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Duplicate Book ID",
                    "This Book ID already exists. Please use another ID."
            );
            return;
        }


        bookIDs.add(normalizedID);

        showAlert(
                Alert.AlertType.INFORMATION,
                "Book Added Successfully",
                "Book details:\n\n"
                        + "Book ID: " + bookID
                        + "\nBook Title: " + title
                        + "\nAuthor: " + author
                        + "\nCategory: " + category
                        + "\nPublished Year: " + year
                        + "\nQuantity: " + quantity
        );

        clearFields();
    }


    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtBookID.clear();
        txtBookTitle.clear();
        txtAuthor.clear();
        txtPublishedYear.clear();
        txtQuantity.clear();
        comboBox.getSelectionModel().clearSelection();
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
                        "Cannot find FXML file:\n/View/" + fxmlFile
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
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("return_book.fxml");
    }


    @FXML
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        loadPage("borrowing_history.fxml");
    }


    @FXML
    void btnLoginOnAction(ActionEvent event) {
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