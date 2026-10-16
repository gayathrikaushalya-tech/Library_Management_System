package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AddBookPageController {

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
    private Button btnLogin;

    @FXML
    private Button btnManageMembers;

    @FXML
    private Button btnReturnBook;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField txtAuthor;

    @FXML
    private TextField txtBookID;

    @FXML
    private TextField txtBookTitle;

    @FXML
    private Label txtLogoutIcon;

    @FXML
    private TextField txtPublishedYear;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Label txtTiltle;

    @FXML
    private Label txtUserIcon;


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

        String category = (String) comboBox.getValue();


        if (bookID.isEmpty() || title.isEmpty()
                || author.isEmpty() || yearText.isEmpty()
                || quantityText.isEmpty() || category == null) {

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

            if (year < 1 || year > 9999) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Invalid Year",
                        "Enter a valid published year."
                );
                return;
            }

        } catch (NumberFormatException e) {
            showAlert(
                    Alert.AlertType.ERROR,
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
                        Alert.AlertType.ERROR,
                        "Invalid Quantity",
                        "Quantity must be greater than zero."
                );
                return;
            }

        } catch (NumberFormatException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Quantity",
                    "Quantity must contain numbers only."
            );
            return;
        }


        if (bookIDs.contains(bookID)) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Duplicate Book ID",
                    "This Book ID already exists."
            );
            return;
        } else {
            bookIDs.add(bookID);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Book added successfully!\n\n"
                            + "Book ID: " + bookID
                            + "\nTitle: " + title
                            + "\nAuthor: " + author
                            + "\nCategory: " + category
                            + "\nPublished Year: " + year
                            + "\nQuantity: " + quantity
            );

            clearFields();
        }

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

        if (comboBox != null) {
            comboBox.getSelectionModel().clearSelection();
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
            java.net.URL resource =
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
    void btnAddMemberOnAction(ActionEvent event) {
        loadPage("add_member_page.fxml");
    }


    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("manage_members_page.fxml");
    }


    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        loadPage("issue_book_page.fxml");
    }


    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("return_book_page.fxml");
    }


    @FXML
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        loadPage("borrowing_history_page.fxml");
    }


    @FXML
    void btnLoginOnAction(ActionEvent event) {
       loadPage("login_page.fxml");
    }

}