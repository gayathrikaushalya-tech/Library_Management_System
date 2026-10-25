package Controller;

import DAO.BookDAO;
import Model.Book;
import Util.PageNavigator;
import Util.NavigatableController;
import java.sql.SQLException;
import java.time.Year;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AddBookPageController  implements NavigatableController {

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

    private final BookDAO bookDAO = new BookDAO();


    private PageNavigator navigator;

    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }

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


        txtBookID.setEditable(false);
        txtBookID.setPromptText("Auto-generated");
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {

        String title = txtBookTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String yearText = txtPublishedYear.getText().trim();
        String quantityText = txtQuantity.getText().trim();
        String category = comboBox.getValue();

        if (title.isEmpty() || author.isEmpty()
                || yearText.isEmpty() || quantityText.isEmpty()
                || category == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all fields and select a category."
            );
            return;
        }

        int year;
        int quantity;

        try {
            year = Integer.parseInt(yearText);

            if (year < 1 || year > Year.now().getValue()) {
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

        try {

            Book book = new Book(
                    title,
                    author,
                    category,
                    year,
                    quantity
            );

            bookDAO.addBook(book);

            boolean saved = bookDAO.addBook(book);

            if (saved) {
                txtBookID.setText(book.getBookCode());

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Book Added Successfully",
                        "Book ID: " + book.getBookCode()
                                + "\nBook Title: " + title
                                + "\nAuthor: " + author
                                + "\nCategory: " + category
                                + "\nPublished Year: " + year
                                + "\nQuantity: " + quantity
                );

                clearFields();
            }

        } catch (SQLException e) {
            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Could not save the book.\n" + e.getMessage()
            );
        } catch (RuntimeException e) {
            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Application Error",
                    "Could not add the book. Check the Book model and DAO."
            );
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


    private void navigateTo(String pageName) {
        if (navigator == null) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "PageNavigator has not been initialized. "
                            + "Pass the shared navigator from the dashboard."
            );
            return;
        }

        navigator.navigate(pageName);
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        navigateTo("dashboard_home");
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
    void btnIssueBookOnAction(ActionEvent event) {
        navigateTo("issue_book");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        navigateTo("manage_members");
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
            navigateTo("login_page");
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