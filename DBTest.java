import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try (Connection conn = DBConnector.getConnection()) {
            System.out.println("✅ Connected to database successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
