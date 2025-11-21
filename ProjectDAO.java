import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public boolean postProject(int userId, String title, String shortDescription, InputStream pdfFile, String pdfFilename) {
        String sql = "INSERT INTO projects (user_id, title, short_description, pdf_file, pdf_filename) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, shortDescription);
            pstmt.setBinaryStream(4, pdfFile);
            pstmt.setString(5, pdfFilename);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT project_id, user_id, title, short_description, pdf_filename FROM projects";
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Project project = new Project();
                project.setProjectId(rs.getInt("project_id"));
                project.setUserId(rs.getInt("user_id"));
                project.setTitle(rs.getString("title"));
                project.setShortDescription(rs.getString("short_description"));
                project.setPdfFilename(rs.getString("pdf_filename"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public byte[] getProjectPdf(int projectId) {
        String sql = "SELECT pdf_file FROM projects WHERE project_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes("pdf_file");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Implement search and filter methods here if needed
}