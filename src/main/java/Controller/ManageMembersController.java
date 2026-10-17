package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import Model.Member;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class ManageMembersController {

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnDashboard;
    @FXML private Button btnDeleteOnAction;
    @FXML private Button btnEdit;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnLogoutIcon;
    @FXML private Button btnManageMembers;
    @FXML private Button btnReturnBook;
    @FXML private Button btnSearch;
    @FXML private Button btnUserIcon;

    @FXML private Pagination pegination;
    @FXML private AnchorPane rootPane;
    @FXML private TableView<Member> tblMembers;
    @FXML private TextField txtSearch;

    private static final int ROWS_PER_PAGE = 5;

    private final ObservableList<Member> allMembers =
            FXCollections.observableArrayList();

    private List<Member> filteredMembers;



    @FXML
    public void initialize() {

        createTableColumns();
        loadSampleData();

        filteredMembers = allMembers.stream()
                .collect(Collectors.toList());

        pegination.setPageFactory(pageIndex -> {
            displayPage(pageIndex);
            return new AnchorPane();
        });

        updatePagination();
    }


    private void createTableColumns() {

        TableColumn<Member, String> colMemberId =
                new TableColumn<>("Member ID");

        colMemberId.setCellValueFactory(
                new PropertyValueFactory<>("memberId"));

        TableColumn<Member, String> colName =
                new TableColumn<>("Full Name");

        colName.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));

        TableColumn<Member, String> colEmail =
                new TableColumn<>("Email");

        colEmail.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        TableColumn<Member, String> colPhone =
                new TableColumn<>("Phone Number");

        colPhone.setCellValueFactory(
                new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Member, String> colAddress =
                new TableColumn<>("Address");

        colAddress.setCellValueFactory(
                new PropertyValueFactory<>("address"));

        colMemberId.setPrefWidth(90);
        colName.setPrefWidth(140);
        colEmail.setPrefWidth(170);
        colPhone.setPrefWidth(120);
        colAddress.setPrefWidth(140);

        tblMembers.getColumns().setAll(
                colMemberId,
                colName,
                colEmail,
                colPhone,
                colAddress
        );

        tblMembers.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }


    private void loadSampleData() {

        allMembers.setAll(
                new Member("M001", "Nimesha Perera",
                        "nimesha@gmail.com",
                        "0771234567", "Colombo"),

                new Member("M002", "Kasun Silva",
                        "kasun@gmail.com",
                        "0712345678", "Kandy"),

                new Member("M003", "Dulani Fernando",
                        "dulani@gmail.com",
                        "0765432109", "Galle"),

                new Member("M004", "Sanduni Wijesekara",
                        "sanduni@gmail.com",
                        "0787654321", "Matara"),

                new Member("M005", "Tharindu Jayasinghe",
                        "tharindu@gmail.com",
                        "0723456789", "Kurunegala"),

                new Member("M006", "Amali Perera",
                        "amali@gmail.com",
                        "0751234567", "Negombo"),

                new Member("M007", "Ravindu Silva",
                        "ravindu@gmail.com",
                        "0779876543", "Gampaha")
        );
    }


    private void updatePagination() {

        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        (double) filteredMembers.size()
                                / ROWS_PER_PAGE
                )
        );

        pegination.setPageCount(totalPages);

        if (pegination.getCurrentPageIndex() >= totalPages) {
            pegination.setCurrentPageIndex(totalPages - 1);
        } else {
            displayPage(pegination.getCurrentPageIndex());
        }
    }

    private void displayPage(int pageIndex) {

        if (filteredMembers == null) {
            return;
        }

        int fromIndex = pageIndex * ROWS_PER_PAGE;

        if (fromIndex >= filteredMembers.size()) {
            tblMembers.setItems(
                    FXCollections.observableArrayList()
            );
            return;
        }

        int toIndex = Math.min(
                fromIndex + ROWS_PER_PAGE,
                filteredMembers.size()
        );

        ObservableList<Member> pageData =
                FXCollections.observableArrayList(
                        filteredMembers.subList(fromIndex, toIndex)
                );

        tblMembers.setItems(pageData);
    }


    @FXML
    void btnSearchOnAction(ActionEvent event) {

        String keyword = txtSearch.getText()
                .trim()
                .toLowerCase();

        if (keyword.isEmpty()) {
            filteredMembers = allMembers.stream()
                    .collect(Collectors.toList());
        } else {
            filteredMembers = allMembers.stream()
                    .filter(member ->
                            member.getMemberId().toLowerCase()
                                    .contains(keyword)
                                    || member.getFullName().toLowerCase()
                                    .contains(keyword)
                                    || member.getEmail().toLowerCase()
                                    .contains(keyword)
                                    || member.getPhoneNumber().toLowerCase()
                                    .contains(keyword)
                                    || member.getAddress().toLowerCase()
                                    .contains(keyword)
                    )
                    .collect(Collectors.toList());
        }

        pegination.setCurrentPageIndex(0);
        updatePagination();
    }


    @FXML
    void btnEditOnAction(ActionEvent event) {

        Member selected = tblMembers.getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Member Selected",
                    "Please select a member from the table."
            );
            return;
        }

        TextInputDialog nameDialog =
                new TextInputDialog(selected.getFullName());

        nameDialog.setTitle("Edit Member");
        nameDialog.setHeaderText("Edit Member Details");
        nameDialog.setContentText("Full Name:");

        Optional<String> nameResult = nameDialog.showAndWait();

        if (!nameResult.isPresent()) {
            return;
        }

        String newName = nameResult.get().trim();

        if (newName.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Name",
                    "Full name cannot be empty."
            );
            return;
        }

        TextInputDialog emailDialog =
                new TextInputDialog(selected.getEmail());

        emailDialog.setTitle("Edit Member");
        emailDialog.setHeaderText("Edit Member Details");
        emailDialog.setContentText("Email:");

        Optional<String> emailResult = emailDialog.showAndWait();

        if (!emailResult.isPresent()) {
            return;
        }

        String newEmail = emailResult.get().trim();

        if (!newEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Email",
                    "Please enter a valid email address."
            );
            return;
        }

        TextInputDialog phoneDialog =
                new TextInputDialog(selected.getPhoneNumber());

        phoneDialog.setTitle("Edit Member");
        phoneDialog.setHeaderText("Edit Member Details");
        phoneDialog.setContentText("Phone Number:");

        Optional<String> phoneResult = phoneDialog.showAndWait();

        if (!phoneResult.isPresent()) {
            return;
        }

        String newPhone = phoneResult.get().trim();

        if (!newPhone.matches("\\d{10}")) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Phone Number",
                    "Enter a 10-digit phone number."
            );
            return;
        }

        TextInputDialog addressDialog =
                new TextInputDialog(selected.getAddress());

        addressDialog.setTitle("Edit Member");
        addressDialog.setHeaderText("Edit Member Details");
        addressDialog.setContentText("Address:");

        Optional<String> addressResult =
                addressDialog.showAndWait();

        if (!addressResult.isPresent()) {
            return;
        }

        String newAddress = addressResult.get().trim();

        if (newAddress.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Address",
                    "Address cannot be empty."
            );
            return;
        }

        selected.setFullName(newName);
        selected.setEmail(newEmail);
        selected.setPhoneNumber(newPhone);
        selected.setAddress(newAddress);

        tblMembers.refresh();

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Member details updated successfully."
        );
    }


    @FXML
    void btnDeleteOnAction(ActionEvent event) {

        Member selected = tblMembers.getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Member Selected",
                    "Please select a member to delete."
            );
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirm.setTitle("Delete Member");
        confirm.setHeaderText("Confirm Member Deletion");
        confirm.setContentText(
                "Delete member " + selected.getFullName() + "?"
        );

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            allMembers.remove(selected);

            filteredMembers.remove(selected);

            updatePagination();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Deleted",
                    "Member deleted successfully."
            );
        }
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
    void btnManageMembersOnAction(ActionEvent event) {
        loadPage("manage_members.fxml");
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