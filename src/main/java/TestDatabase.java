import DB.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {

        try (Connection connection =
                     DBConnection.getConnection()) {

            System.out.println(
                    "MySQL connected successfully!"
            );

        } catch (SQLException e) {
            System.out.println(
                    "MySQL connection failed!"
            );
            e.printStackTrace();
        }
    }
}
