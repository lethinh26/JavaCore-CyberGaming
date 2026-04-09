package ra.cybergaming.util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("MYSQL_URL");
    private static final String USERNAME = dotenv.get("MYSQL_USERNAME");
    private static final String PASS = dotenv.get("MYSQL_PASSWORD");

    public static Connection openConnection() throws ClassNotFoundException, SQLException {
        if (URL == null || USERNAME == null || PASS == null) {
            throw new SQLException("Lỗi: Kiểm tra file .env");
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USERNAME, PASS);
    }
}
