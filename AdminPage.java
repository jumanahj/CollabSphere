import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class AdminPage extends JFrame {

    private JButton viewStudentsBtn, viewProjectsBtn, exitBtn;

    public AdminPage() {
        setTitle("Admin Dashboard");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background panel with custom color
        JPanel background = new JPanel();
        background.setBackground(new Color(240, 248, 255)); // Alice Blue
        background.setLayout(new BorderLayout());
        add(background);

        // Title section
        JLabel title = new JLabel("📋 Admin Panel - Student Collaboration Platform", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 25, 112)); // Midnight Blue
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        background.add(title, BorderLayout.NORTH);

        // Button panel with vertical layout
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

        viewStudentsBtn = createButton("👥 View Students", new Color(72, 133, 237));
        viewProjectsBtn = createButton("📁 View Projects", new Color(76, 175, 80));
        exitBtn = createButton("❌ Exit", new Color(244, 67, 54));

        centerPanel.add(viewStudentsBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(viewProjectsBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(exitBtn);

        background.add(centerPanel, BorderLayout.CENTER);

        // Actions
        viewStudentsBtn.addActionListener(e -> showAllStudents());
        viewProjectsBtn.addActionListener(e -> showAllProjects());
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showAllStudents() {
        try (Connection conn = DBConnector.getConnection()) {
            String query = "SELECT user_id, name, email, department, year, skills FROM users WHERE role = 'Student'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 200));

            JOptionPane.showMessageDialog(this, scrollPane, "All Students", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving students: " + ex.getMessage());
        }
    }

    private void showAllProjects() {
        ProjectDAO projectDAO = new ProjectDAO();
        List<Project> projects = projectDAO.getAllProjects();

        String[] columnNames = {"Project ID", "User ID", "Title", "Short Description", "PDF File"};
        Object[][] data = new Object[projects.size()][5];

        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            data[i][0] = p.getProjectId();
            data[i][1] = p.getUserId();
            data[i][2] = p.getTitle();
            data[i][3] = p.getShortDescription();
            data[i][4] = p.getPdfFilename();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "All Projects", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPage::new);
    }
}
