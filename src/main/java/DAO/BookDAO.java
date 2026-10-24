package DAO;

import Model.Book;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {


    public boolean addBook(Book book) throws SQLException {

        String insertSQL =
                "INSERT INTO books " +
                        "(title, author, category, isbn, quantity, available_quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        String updateSQL =
                "UPDATE books SET book_code = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int generatedId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getAuthor());
                    ps.setString(3, book.getCategory());
                    ps.setString(4, book.getIsbn());
                    ps.setInt(5, book.getQuantity());
                    ps.setInt(6, book.getQuantity());

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "MySQL did not return a generated ID.");
                        }
                        generatedId = keys.getInt(1);
                    }
                }

                String code = String.format("BK%04d", generatedId);

                try (PreparedStatement ps =
                             con.prepareStatement(updateSQL)) {
                    ps.setString(1, code);
                    ps.setInt(2, generatedId);
                    ps.executeUpdate();
                }

                con.commit();

                book.setId(generatedId);
                book.setBookCode(code);

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Book> getAllBooks() throws SQLException {

        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book book = new Book();

                book.setId(rs.getInt("id"));
                book.setBookCode(rs.getString("book_code"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setIsbn(rs.getString("isbn"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailableQuantity(
                        rs.getInt("available_quantity"));

                books.add(book);
            }
        }

        return books;
    }


    public boolean updateBook(Book book) throws SQLException {

        String sql = "UPDATE books SET title=?, author=?, category=?, " +
                "isbn=?, quantity=?, available_quantity=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setString(4, book.getIsbn());
            ps.setInt(5, book.getQuantity());
            ps.setInt(6, book.getAvailableQuantity());
            ps.setInt(7, book.getId());

            return ps.executeUpdate() > 0;
        }
    }


    public boolean deleteBook(int id) throws SQLException {

        String sql = "DELETE FROM books WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}