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
        try {
            if (URL == null || USERNAME == null || PASS == null) {
                throw new SQLException("Lỗi: Kiểm tra file .env");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASS);
        } catch (SQLException e) {
            switch (e.getSQLState()) {
                case "08001":
                    throw new SQLException("Không thể tạo connection");
                case "28000": // sai auth
                    throw new SQLException("Sai tài khoản/mật khẩu Database");
                case "08S01": // ko connect đc tới db
                    throw new SQLException("Sai đường dẫn Database hoặc Database chưa mở");
                default:
                    throw new SQLException(e.getMessage());
            }
        }
    }

    public static boolean checkDB() {
        try {
            Connection conn = openConnection();
            System.out.println("Kết nối Database thành công!");
            conn.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối Database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy driver JDBC");
        }
        return false;
    }
}