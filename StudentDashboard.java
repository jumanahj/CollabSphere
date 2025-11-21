import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentDashboard extends JFrame {

    private int userId;

    public StudentDashboard(int userId) {
        this.userId = userId;

        setTitle("Student Dashboard");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("🎓 Welcome to Your Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("📁 Joined Projects", createJoinedProjectsPanel());
        tabs.addTab("📬 Join Requests", createJoinRequestsPanel());

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createJoinedProjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Project ID", "Title", "Description", "Skills Match (%)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        String studentSkills = getStudentSkills();

        String sql = "SELECT p.project_id, p.title, p.short_description FROM project_members pm " +
                     "JOIN projects p ON pm.project_id = p.project_id WHERE pm.user_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String description = rs.getString("short_description");
                int matchScore = calculateSkillsMatch(studentSkills, description);

                model.addRow(new Object[]{
                    rs.getInt("project_id"),
                    rs.getString("title"),
                    description,
                    matchScore + "%"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createJoinRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"Project Title", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        String sql = "SELECT p.title, jr.status FROM join_requests jr " +
                     "JOIN projects p ON jr.project_id = p.project_id WHERE jr.user_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("title"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private String getStudentSkills() {
        String sql = "SELECT skills FROM users WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("skills");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int calculateSkillsMatch(String studentSkills, String projectDescription) {
        if (studentSkills == null || projectDescription == null) return 0;

        String[] skillsArray = studentSkills.toLowerCase().split(",");
        int matchCount = 0;

        for (String skill : skillsArray) {
            if (projectDescription.toLowerCase().contains(skill.trim())) {
                matchCount++;
            }
        }

        return skillsArray.length == 0 ? 0 : (int) ((matchCount * 100.0) / skillsArray.length);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard(2)); // Replace 2 with real logged-in user_id
    }
}
