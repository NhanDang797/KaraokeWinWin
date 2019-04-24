
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author nhandang
 */
public class ConnectDB {
    public static Connection getConnect() {
        Connection conn = null;
        try {
            // load driver sql
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // tạo kết nối;
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLKaraoke";
            conn = DriverManager.getConnection(url,"sa","123");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }
}
