package DAO;

import Model.Borrowing;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {


    public boolean issueBook(
            int bookId,
            int memberId,
            Date issueDate,
            Date dueDate) throws SQLException {

        String lockSQL =
                "SELECT available_quantity FROM books " +
                        "WHERE id = ? FOR UPDATE";

        String insertSQL =
                "INSERT INTO borrowings " +
                        "(book_id, member_id, issue_date, due_date, status) " +
                        "VALUES (?, ?, ?, ?, 'ISSUED')";

        String codeSQL =
                "UPDATE borrowings SET borrowing_code = ? WHERE id = ?";

        String updateBookSQL =
                "UPDATE books SET available_quantity = " +
                        "available_quantity - 1 WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             con.prepareStatement(lockSQL)) {
                    ps.setInt(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Book not found.");
                        }

                        if (rs.getInt("available_quantity") <= 0) {
                            throw new SQLException(
                                    "No copies of this book are available.");
                        }
                    }
                }

                int borrowingId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setDate(3, issueDate);
                    ps.setDate(4, dueDate);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Could not generate borrowing ID.");
                        }
                        borrowingId = rs.getInt(1);
                    }
                }

                String code = String.format("BR%04d", borrowingId);

                try (PreparedStatement ps =
                             con.prepareStatement(codeSQL)) {
                    ps.setString(1, code);
                    ps.setInt(2, borrowingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateBookSQL)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean returnBook(String borrowingCode)
            throws SQLException {

        String findSQL =
                "SELECT book_id FROM borrowings " +
                        "WHERE borrowing_code = ? AND status = 'ISSUED' " +
                        "FOR UPDATE";

        String returnSQL =
                "UPDATE borrowings SET status = 'RETURNED', " +
                        "return_date = CURRENT_DATE " +
                        "WHERE borrowing_code = ? AND status = 'ISSUED'";

        String updateBookSQL =
                "UPDATE books SET available_quantity = " +
                        "available_quantity + 1 WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int bookId;

                try (PreparedStatement ps =
                             con.prepareStatement(findSQL)) {
                    ps.setString(1, borrowingCode);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Active borrowing record not found.");
                        }
                        bookId = rs.getInt("book_id");
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(returnSQL)) {
                    ps.setString(1, borrowingCode);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not complete book return.");
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateBookSQL)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Borrowing> getAllBorrowings() throws SQLException {

        List<Borrowing> records = new ArrayList<>();

        String sql =
                "SELECT br.id, br.borrowing_code, br.book_id, " +
                        "br.member_id, b.title AS book_title, " +
                        "m.full_name AS member_name, br.issue_date, " +
                        "br.due_date, br.return_date, br.status " +
                        "FROM borrowings br " +
                        "JOIN books b ON br.book_id = b.id " +
                        "JOIN members m ON br.member_id = m.id " +
                        "ORDER BY br.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Borrowing record = new Borrowing();

                record.setId(rs.getInt("id"));
                record.setBorrowingCode(
                        rs.getString("borrowing_code"));
                record.setBookId(rs.getInt("book_id"));
                record.setMemberId(rs.getInt("member_id"));
                record.setBookTitle(rs.getString("book_title"));
                record.setMemberName(rs.getString("member_name"));

                Date issueDate = rs.getDate("issue_date");
                Date dueDate = rs.getDate("due_date");
                Date returnDate = rs.getDate("return_date");

                if (issueDate != null) {
                    record.setIssueDate(issueDate.toLocalDate());
                }

                if (dueDate != null) {
                    record.setDueDate(dueDate.toLocalDate());
                }

                if (returnDate != null) {
                    record.setReturnDate(returnDate.toLocalDate());
                }

                String dbStatus = rs.getString("status");

                if ("RETURNED".equalsIgnoreCase(dbStatus)) {
                    record.setStatus("Returned");
                } else if (dueDate != null
                        && dueDate.before(
                        Date.valueOf(java.time.LocalDate.now()))) {
                    record.setStatus("Overdue");
                } else {
                    record.setStatus("Borrowed");
                }

                records.add(record);
            }
        }

        return records;
    }
}