import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:gym_management.db";

    // Initialize Database
    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS members (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, phone TEXT, plan TEXT)";
            conn.createStatement().execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // CREATE
    public static boolean addMember(String name, String phone, String plan) {
        String sql = "INSERT INTO members(name, phone, plan) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, plan);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (Load data into the table)
    public static void loadMembers(DefaultTableModel tableModel, String search) {
        tableModel.setRowCount(0); // Clear table
        String sql = "SELECT * FROM members WHERE name LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + search + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(rs.getInt("id")));
                row.add(rs.getString("name"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("plan"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // UPDATE
    public static boolean updateMember(int id, String name, String phone, String plan) {
        String sql = "UPDATE members SET name=?, phone=?, plan=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, plan);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public static boolean deleteMember(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM members WHERE id=?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}