package Controller;

import DAO.BorrowingDAO;
import Model.Borrowing;
import Util.NavigatableController;
import Util.PageNavigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BorrowingHistoryController
        implements NavigatableController {

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

    @FXML private TableColumn<BorrowRecord, String> colBookTitle;
    @FXML private TableColumn<BorrowRecord, String> colBorrowDate;
    @FXML private TableColumn<BorrowRecord, String> colBorrowedID;
    @FXML private TableColumn<BorrowRecord, String> colDueDate;
    @FXML private TableColumn<BorrowRecord, String> colMemberName;
    @FXML private TableColumn<BorrowRecord, String> colReturnDate;
    @FXML private TableColumn<BorrowRecord, String> colStatus;

    @FXML private Pagination lblRecordingInfo;
    @FXML private TableView<BorrowRecord> tblBorrowingHistory;
    @FXML private TextField txtSearch;

    private static final int ROWS_PER_PAGE = 5;

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    private PageNavigator navigator;

    private final ObservableList<BorrowRecord> allRecords =
            FXCollections.observableArrayList();

    private List<BorrowRecord> filteredRecords = new ArrayList<>();

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }

    public static class BorrowRecord {

        private final String borrowedID;
        private final String bookTitle;
        private final String memberName;
        private final String borrowDate;
        private final String dueDate;
        private final String returnDate;
        private final String status;

        public BorrowRecord(
                String borrowedID,
                String bookTitle,
                String memberName,
                String borrowDate,
                String dueDate,
                String returnDate,
                String status) {

            this.borrowedID = borrowedID;
            this.bookTitle = bookTitle;
            this.memberName = memberName;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.status = status;
        }

        public String getBorrowedID() {
            return borrowedID;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getMemberName() {
            return memberName;
        }

        public String getBorrowDate() {
            return borrowDate;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getReturnDate() {
            return returnDate;
        }

        public String getStatus() {
            return status;
        }
    }

    @FXML
    public void initialize() {

        createTableColumns();
        loadRecordsFromDatabase();

        filteredRecords = new ArrayList<>(allRecords);

        lblRecordingInfo.setPageFactory(pageIndex -> {
            displayPage(pageIndex);
            return new javafx.scene.layout.AnchorPane();
        });

        updatePagination();
    }

    private void createTableColumns() {

        colBorrowedID.setCellValueFactory(
                new PropertyValueFactory<>("borrowedID"));

        colBookTitle.setCellValueFactory(
                new PropertyValueFactory<>("bookTitle"));

        colMemberName.setCellValueFactory(
                new PropertyValueFactory<>("memberName"));

        colBorrowDate.setCellValueFactory(
                new PropertyValueFactory<>("borrowDate"));

        colDueDate.setCellValueFactory(
                new PropertyValueFactory<>("dueDate"));

        colReturnDate.setCellValueFactory(
                new PropertyValueFactory<>("returnDate"));

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        tblBorrowingHistory.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRecordsFromDatabase() {

        allRecords.clear();

        try {
            List<Borrowing> records =
                    borrowingDAO.getAllBorrowings();

            for (Borrowing record : records) {
                allRecords.add(new BorrowRecord(
                        record.getBorrowingCode(),
                        record.getBookTitle(),
                        record.getMemberName(),
                        formatDate(record.getIssueDate()),
                        formatDate(record.getDueDate()),
                        formatDate(record.getReturnDate()),
                        record.getStatus()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to load borrowing history.\n"
                            + e.getMessage());
        }
    }

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "-" : date.toString();
    }

    private void updatePagination() {

        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        (double) filteredRecords.size()
                                / ROWS_PER_PAGE));

        lblRecordingInfo.setPageCount(totalPages);

        if (lblRecordingInfo.getCurrentPageIndex() >= totalPages) {
            lblRecordingInfo.setCurrentPageIndex(totalPages - 1);
        }

        displayPage(lblRecordingInfo.getCurrentPageIndex());
    }

    private void displayPage(int pageIndex) {

        int fromIndex = pageIndex * ROWS_PER_PAGE;

        if (fromIndex >= filteredRecords.size()) {
            tblBorrowingHistory.setItems(
                    FXCollections.observableArrayList());
            return;
        }

        int toIndex = Math.min(
                fromIndex + ROWS_PER_PAGE,
                filteredRecords.size());

        tblBorrowingHistory.setItems(
                FXCollections.observableArrayList(
                        filteredRecords.subList(fromIndex, toIndex)));
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {

        String keyword = txtSearch.getText()
                .trim()
                .toLowerCase();

        if (keyword.isEmpty()) {
            filteredRecords = new ArrayList<>(allRecords);
        } else {
            filteredRecords = allRecords.stream()
                    .filter(record ->
                            record.getBorrowedID()
                                    .toLowerCase().contains(keyword)
                                    || record.getBookTitle()
                                    .toLowerCase().contains(keyword)
                                    || record.getMemberName()
                                    .toLowerCase().contains(keyword)
                                    || record.getStatus()
                                    .toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        lblRecordingInfo.setCurrentPageIndex(0);
        updatePagination();
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
    void btnIssueBookOnAction(ActionEvent event) {
        navigateTo("issue_book");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        navigateTo("return_book");
    }

    @FXML
    void btnManageMembersOnAction(ActionEvent event) {
        navigateTo("manage_members");
    }

    @FXML
    void btnBorrowingHistoryOnACtion(ActionEvent event) {
        navigateTo("borrowing_history");
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
                "Logged-in user: Admin");
    }

    private void navigateTo(String pageName) {

        if (navigator == null) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "The shared PageNavigator has not been initialized.");
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