package Controller;

import DAO.BookDAO;
import DAO.MemberDAO;
import DAO.BorrowingDAO;
import Model.Book;
import Model.Member;
import Util.NavigatableController;
import Util.PageNavigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class IssueBookController implements NavigatableController {

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnClear;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssue;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnLogoutIcon;
    @FXML private Button btnManageMembers;
    @FXML private Button btnReturnBook;
    @FXML private Button btnUserIcon;

    @FXML private ComboBox<String> cmbBook;
    @FXML private ComboBox<String> cmbMember;
    @FXML private DatePicker dpDueDate;
    @FXML private DatePicker dpIssueDate;
    @FXML private AnchorPane rootPane;

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    private PageNavigator navigator;
    private List<Book> books;
    private List<Member> members;

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {

        dpIssueDate.setValue(LocalDate.now());
        dpDueDate.setValue(LocalDate.now().plusDays(14));

        dpDueDate.setDayCellFactory(picker ->
                new DateCell() {
                    @Override
                    public void updateItem(
                            LocalDate date, boolean empty) {

                        super.updateItem(date, empty);

                        LocalDate issueDate = dpIssueDate.getValue();

                        setDisable(empty || issueDate == null
                                || date.isBefore(issueDate));
                    }
                });

        loadBooksAndMembers();
    }

    private void loadBooksAndMembers() {
        try {
            books = bookDAO.getAllBooks();
            members = memberDAO.getAllMembers();

            cmbBook.getItems().clear();

            for (Book book : books) {
                if (book.getAvailableQuantity() > 0) {
                    cmbBook.getItems().add(
                            book.getBookCode() + " - "
                                    + book.getTitle()
                                    + " (Available: "
                                    + book.getAvailableQuantity() + ")");
                }
            }

            cmbMember.getItems().clear();

            for (Member member : members) {
                cmbMember.getItems().add(
                        member.getMemberId() + " - "
                                + member.getFullName());
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to load books and members.\n"
                            + e.getMessage());
        }
    }

    @FXML
    void btnIssueOnAction(ActionEvent event) {

        String selectedBook = cmbBook.getValue();
        String selectedMember = cmbMember.getValue();
        LocalDate issueDate = dpIssueDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();

        if (selectedBook == null || selectedMember == null
                || issueDate == null || dueDate == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Missing Information",
                    "Select a book, a member, an issue date, "
                            + "and a due date.");
            return;
        }

        if (dueDate.isBefore(issueDate)) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid Due Date",
                    "The due date cannot be before the issue date.");
            return;
        }

        String bookCode = selectedBook.split(" - ", 2)[0];
        String memberCode = selectedMember.split(" - ", 2)[0];

        Book selectedBookObject = books.stream()
                .filter(book -> bookCode.equals(book.getBookCode()))
                .findFirst().orElse(null);

        Member selectedMemberObject = members.stream()
                .filter(member ->
                        memberCode.equals(member.getMemberId()))
                .findFirst().orElse(null);

        if (selectedBookObject == null
                || selectedMemberObject == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Selection Error",
                    "The selected book or member could not be found.");
            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Issue Book");
        confirmation.setHeaderText("Confirm Book Issue");
        confirmation.setContentText(
                "Book: " + selectedBookObject.getTitle()
                        + "\nMember: "
                        + selectedMemberObject.getFullName()
                        + "\nIssue Date: " + issueDate
                        + "\nDue Date: " + dueDate);

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try {
                boolean success = borrowingDAO.issueBook(
                        selectedBookObject.getId(),
                        selectedMemberObject.getId(),
                        java.sql.Date.valueOf(issueDate),
                        java.sql.Date.valueOf(dueDate));

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Success",
                            "Book issued successfully.");

                    clearFields();
                    loadBooksAndMembers();
                }

            } catch (SQLException e) {
                e.printStackTrace();

                showAlert(Alert.AlertType.ERROR,
                        "Issue Failed",
                        "Unable to issue this book.\n"
                                + e.getMessage());
            }
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

        if (issueDate != null
                && (dpDueDate.getValue() == null
                || dpDueDate.getValue().isBefore(issueDate))) {
            dpDueDate.setValue(issueDate.plusDays(14));
        }
    }

    @FXML
    void dpDueDateOnAction(ActionEvent event) {
        LocalDate issueDate = dpIssueDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();

        if (issueDate != null && dueDate != null
                && dueDate.isBefore(issueDate)) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid Due Date",
                    "Choose a due date on or after the issue date.");
            dpDueDate.setValue(issueDate.plusDays(14));
        }
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
    void btnBorrowingHistoryOnACtion(ActionEvent event) {
        navigateTo("borrowing_history");
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
    void btnIssueBookOnAction(ActionEvent event) {
        navigateTo("issue_book");
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        logout();
    }

    @FXML
    void btnLogoutIconOnAction(ActionEvent event) {
        logout();
    }

    @FXML
    void btnUserIconOnAction(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION,
                "User Information", "Logged-in user: Admin");
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

    private void navigateTo(String pageName) {
        if (navigator == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Navigation Error",
                    "The shared navigator was not initialized.");
            return;
        }

        navigator.navigate(pageName);
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