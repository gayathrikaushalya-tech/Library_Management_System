package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BorrowingHistoryController {

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

    private final ObservableList<BorrowRecord> allRecords =
            FXCollections.observableArrayList();

    private List<BorrowRecord> filteredRecords =
            new ArrayList<>();

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
        loadSampleData();

        filteredRecords = new ArrayList<>(allRecords);

        lblRecordingInfo.setPageFactory(pageIndex -> {
            displayPage(pageIndex);
            return new AnchorPane();
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
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void loadSampleData() {

        allRecords.clear();

        LocalDate today = LocalDate.now();

        allRecords.add(new BorrowRecord(
                "BR001",
                "Harry Potter",
                "Nimesha Perera",
                today.minusDays(20).toString(),
                today.minusDays(6).toString(),
                "-",
                "Overdue"
        ));

        allRecords.add(new BorrowRecord(
                "BR002",
                "Introduction to Java",
                "Kasun Silva",
                today.minusDays(3).toString(),
                today.plusDays(11).toString(),
                "-",
                "Borrowed"
        ));

        allRecords.add(new BorrowRecord(
                "BR003",
                "Clean Code",
                "Dulani Fernando",
                today.minusDays(15).toString(),
                today.minusDays(1).toString(),
                today.toString(),
                "Returned"
        ));

        allRecords.add(new BorrowRecord(
                "BR004",
                "Database Systems",
                "Sanduni Wijesekara",
                today.minusDays(2).toString(),
                today.plusDays(12).toString(),
                "-",
                "Borrowed"
        ));

        allRecords.add(new BorrowRecord(
                "BR005",
                "Computer Networks",
                "Tharindu Jayasinghe",
                today.minusDays(25).toString(),
                today.minusDays(11).toString(),
                today.minusDays(12).toString(),
                "Returned"
        ));

        allRecords.add(new BorrowRecord(
                "BR006",
                "Operating Systems",
                "Amali Perera",
                today.minusDays(18).toString(),
                today.minusDays(4).toString(),
                "-",
                "Overdue"
        ));

        allRecords.add(new BorrowRecord(
                "BR007",
                "Software Engineering",
                "Ravindu Silva",
                today.minusDays(1).toString(),
                today.plusDays(13).toString(),
                "-",
                "Borrowed"
        ));
    }

    private void updatePagination() {

        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        (double) filteredRecords.size()
                                / ROWS_PER_PAGE
                )
        );

        lblRecordingInfo.setPageCount(totalPages);

        int currentPage = lblRecordingInfo.getCurrentPageIndex();

        if (currentPage >= totalPages) {
            lblRecordingInfo.setCurrentPageIndex(totalPages - 1);
        } else {
            displayPage(currentPage);
        }
    }

    private void displayPage(int pageIndex) {

        int fromIndex = pageIndex * ROWS_PER_PAGE;

        if (fromIndex >= filteredRecords.size()) {
            tblBorrowingHistory.setItems(
                    FXCollections.observableArrayList()
            );
            return;
        }

        int toIndex = Math.min(
                fromIndex + ROWS_PER_PAGE,
                filteredRecords.size()
        );

        ObservableList<BorrowRecord> pageData =
                FXCollections.observableArrayList(
                        filteredRecords.subList(fromIndex, toIndex)
                );

        tblBorrowingHistory.setItems(pageData);
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
                                    .toLowerCase().contains(keyword)
                    )
                    .collect(Collectors.toList());
        }

        lblRecordingInfo.setCurrentPageIndex(0);
        updatePagination();
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
    void btnReturnBookOnAction(ActionEvent event) {
        loadPage("return_book.fxml");
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

            AnchorPane rootPane = findRootPane();

            if (rootPane == null) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Navigation Error",
                        "Root navigation pane was not found."
                );
                return;
            }

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

    private AnchorPane findRootPane() {

        return (AnchorPane) tblBorrowingHistory.getScene()
                .lookup("#rootPane");
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
