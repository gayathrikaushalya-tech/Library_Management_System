package Controller;

import DAO.MemberDAO;
import Model.Member;
import Util.NavigatableController;
import Util.PageNavigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ManageMembersController implements NavigatableController {

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

    private final MemberDAO memberDAO = new MemberDAO();

    private final ObservableList<Member> allMembers =
            FXCollections.observableArrayList();

    private List<Member> filteredMembers;

    private PageNavigator navigator;

    private final ContextMenu searchSuggestions = new ContextMenu();

    @Override
    public void setNavigator(PageNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        createTableColumns();

        pegination.setPageFactory(pageIndex -> {
            displayPage(pageIndex);
            return new AnchorPane();
        });


        txtSearch.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pegination.setCurrentPageIndex(0);
                    applySearch();
                    showSearchSuggestions(newValue);
                }
        );

        loadMembersFromDatabase();
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

    private void loadMembersFromDatabase() {
        try {
            allMembers.setAll(memberDAO.getAllMembers());
            applySearch();
        } catch (SQLException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to load members from MySQL.\n"
                            + e.getMessage()
            );
        }
    }

    private void applySearch() {
        String keyword = txtSearch == null
                || txtSearch.getText() == null
                ? ""
                : txtSearch.getText().trim().toLowerCase();

        filteredMembers = allMembers.stream()
                .filter(member -> {
                    if (keyword.isEmpty()) {
                        return true;
                    }

                    return safe(member.getMemberId()).contains(keyword)
                            || safe(member.getFullName()).contains(keyword)
                            || safe(member.getEmail()).contains(keyword)
                            || safe(member.getPhoneNumber()).contains(keyword)
                            || safe(member.getAddress()).contains(keyword);
                })
                .collect(Collectors.toList());

        updatePagination();
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void updatePagination() {
        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        (double) filteredMembers.size() / ROWS_PER_PAGE
                )
        );

        pegination.setPageCount(totalPages);

        int currentPage = Math.min(
                pegination.getCurrentPageIndex(),
                totalPages - 1
        );

        pegination.setCurrentPageIndex(currentPage);
        displayPage(currentPage);
    }

    private void displayPage(int pageIndex) {
        if (filteredMembers == null) {
            tblMembers.setItems(
                    FXCollections.observableArrayList()
            );
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

        tblMembers.setItems(
                FXCollections.observableArrayList(
                        filteredMembers.subList(fromIndex, toIndex)
                )
        );
    }

    private void showSearchSuggestions(String input) {
        searchSuggestions.getItems().clear();

        if (input == null || input.trim().isEmpty()
                || txtSearch.getScene() == null
                || txtSearch.getScene().getWindow() == null
                || !txtSearch.getScene().getWindow().isShowing()) {
            searchSuggestions.hide();
            return;
        }

        String keyword = input.trim().toLowerCase();

        List<Member> matches = allMembers.stream()
                .filter(member ->
                        safe(member.getMemberId()).contains(keyword)
                                || safe(member.getFullName()).contains(keyword)
                                || safe(member.getEmail()).contains(keyword)
                                || safe(member.getPhoneNumber()).contains(keyword)
                                || safe(member.getAddress()).contains(keyword)
                )
                .limit(8)
                .collect(Collectors.toList());

        for (Member member : matches) {
            MenuItem item = new MenuItem(
                    member.getMemberId() + " - " + member.getFullName()
            );

            item.setOnAction(event -> {
                txtSearch.setText(member.getMemberId());
                txtSearch.positionCaret(txtSearch.getText().length());

                applySearch();
                searchSuggestions.hide();


                tblMembers.getSelectionModel().clearSelection();
                tblMembers.getSelectionModel().select(member);
                tblMembers.scrollTo(member);
            });

            searchSuggestions.getItems().add(item);
        }

        if (searchSuggestions.getItems().isEmpty()) {
            searchSuggestions.hide();
        } else {
            searchSuggestions.show(txtSearch, Side.BOTTOM, 0, 0);
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        pegination.setCurrentPageIndex(0);
        applySearch();
    }

    @FXML
    void btnEditOnAction(ActionEvent event) {
        Member selected =
                tblMembers.getSelectionModel().getSelectedItem();

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
                new TextInputDialog(
                        selected.getEmail() == null
                                ? ""
                                : selected.getEmail()
                );
        emailDialog.setTitle("Edit Member");
        emailDialog.setHeaderText("Edit Member Details");
        emailDialog.setContentText("Email:");

        Optional<String> emailResult = emailDialog.showAndWait();
        if (!emailResult.isPresent()) {
            return;
        }

        String newEmail = emailResult.get().trim();

        if (!newEmail.isEmpty()
                && !newEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Email",
                    "Enter a valid email address or leave it empty."
            );
            return;
        }

        TextInputDialog phoneDialog =
                new TextInputDialog(
                        selected.getPhoneNumber() == null
                                ? ""
                                : selected.getPhoneNumber()
                );
        phoneDialog.setTitle("Edit Member");
        phoneDialog.setHeaderText("Edit Member Details");
        phoneDialog.setContentText("Phone Number:");

        Optional<String> phoneResult = phoneDialog.showAndWait();
        if (!phoneResult.isPresent()) {
            return;
        }

        String newPhone = phoneResult.get().trim();

        if (!newPhone.isEmpty()
                && !newPhone.matches("\\d{10}")) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Phone Number",
                    "Enter a 10-digit phone number or leave it empty."
            );
            return;
        }

        TextInputDialog addressDialog =
                new TextInputDialog(
                        selected.getAddress() == null
                                ? ""
                                : selected.getAddress()
                );
        addressDialog.setTitle("Edit Member");
        addressDialog.setHeaderText("Edit Member Details");
        addressDialog.setContentText("Address:");

        Optional<String> addressResult = addressDialog.showAndWait();
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


        String oldName = selected.getFullName();
        String oldEmail = selected.getEmail();
        String oldPhone = selected.getPhoneNumber();
        String oldAddress = selected.getAddress();

        selected.setFullName(newName);
        selected.setEmail(newEmail);
        selected.setPhoneNumber(newPhone);
        selected.setAddress(newAddress);

        try {
            if (memberDAO.updateMember(selected)) {
                loadMembersFromDatabase();

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Member details updated successfully."
                );
            } else {
                selected.setFullName(oldName);
                selected.setEmail(oldEmail);
                selected.setPhoneNumber(oldPhone);
                selected.setAddress(oldAddress);

                showAlert(
                        Alert.AlertType.WARNING,
                        "Not Updated",
                        "No member record was updated."
                );
            }
        } catch (SQLException e) {
            selected.setFullName(oldName);
            selected.setEmail(oldEmail);
            selected.setPhoneNumber(oldPhone);
            selected.setAddress(oldAddress);

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Unable to update member.\n" + e.getMessage()
            );
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Member selected =
                tblMembers.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Member Selected",
                    "Please select a member to delete."
            );
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Member");
        confirm.setHeaderText("Confirm Member Deletion");
        confirm.setContentText(
                "Delete member " + selected.getFullName()
                        + " (" + selected.getMemberId() + ")?"
        );

        Optional<ButtonType> result = confirm.showAndWait();

        if (!result.isPresent()
                || result.get() != ButtonType.OK) {
            return;
        }

        try {
            if (memberDAO.deleteMember(selected.getId())) {
                loadMembersFromDatabase();

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Deleted",
                        "Member deleted successfully."
                );
            } else {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Not Deleted",
                        "The member record was not found."
                );
            }
        } catch (SQLException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Delete Member",
                    "The member may have borrowing records. "
                            + "Check the database constraints.\n"
                            + e.getMessage()
            );
        }
    }

    private void navigate(String page) {
        if (navigator != null) {
            navigator.navigate(page);
        } else {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    "PageNavigator was not injected into this controller."
            );
        }
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
    void btnManageMembersOnAction(ActionEvent event) {
        navigate("manage_members");
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        navigate("issue_book");
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

        if (result.isPresent() && result.get() == ButtonType.OK) {
            navigate("login_page");
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