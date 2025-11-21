import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(String role, String name, String email, String password, String department, String year, String skills) {
        String sql = "INSERT INTO users (role, name, email, password, department, year, skills) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, password); // INSECURE: Replace with password hashing!
            pstmt.setString(5, department);
            pstmt.setString(6, year);
            pstmt.setString(7, skills);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String role, String email, String password) {
        String sql = "SELECT user_id, name, email, department, year, skills FROM users WHERE role = ? AND email = ? AND password = ?"; // INSECURE: Plain text comparison!
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setString(2, email);
            pstmt.setString(3, password); // INSECURE: Plain text comparison!
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setRole(role);
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setDepartment(rs.getString("department"));
                user.setYear(rs.getString("year"));
                user.setSkills(rs.getString("skills"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserProfile(int userId, String name, String email, String department, String year, String skills) {
        String sql = "UPDATE users SET name = ?, email = ?, department = ?, year = ?, skills = ? WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, department);
            pstmt.setString(4, year);
            pstmt.setString(5, skills);
            pstmt.setInt(6, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?"; // INSECURE: Plain text storage!
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword); // INSECURE: Should be hashed!
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, role, name, email, department, year, skills FROM users WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setDepartment(rs.getString("department"));
                user.setYear(rs.getString("year"));
                user.setSkills(rs.getString("skills"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}