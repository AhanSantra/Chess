import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class utils {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://db.bboosbqnodjhgggpfbmz.supabase.co:5432/postgres?sslmode=require";
        String user = "postgres";
        String password = "741@AhanSantra";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to Supabase!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NOW();");

            if (rs.next()) {
                System.out.println("Server time: " + rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
