import Util.DBConnection;
import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {
        try (Connection connection =
                     DBConnection.getConnection()) {

            System.out.println(
                    "MySQL connected successfully!"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}