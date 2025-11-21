import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class BrowseProjectsPage extends JFrame {
    private User loggedInUser;
    private JTable projectTable;
    private DefaultTableModel tableModel;

    public BrowseProjectsPage(User user) {
        this.loggedInUser = user;

        setTitle("Browse Projects");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new Object[]{"Title", "Description", "Download", "Request Join"}, 0);
        projectTable = new JTable(tableModel);
        projectTable.setRowHeight(30);

        fetchProjects();

        projectTable.getColumn("Download").setCellRenderer(new ButtonRenderer());
        projectTable.getColumn("Download").setCellEditor(new ButtonEditor(new JCheckBox(), this, "download"));

        projectTable.getColumn("Request Join").setCellRenderer(new ButtonRenderer());
        projectTable.getColumn("Request Join").setCellEditor(new ButtonEditor(new JCheckBox(), this, "join"));

        JScrollPane scrollPane = new JScrollPane(projectTable);
        add(scrollPane);

        setVisible(true);
    }

    private void fetchProjects() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_platform", "root", "root789")) {
            String sql = "SELECT project_id, title, short_description, pdf_file, user_id FROM projects";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("title"));
                row.add(rs.getString("short_description"));
                row.add("Download");
                row.add("Request Join");
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching projects.");
        }
    }

    public void handleDownloadPDF(int row) {
        JOptionPane.showMessageDialog(this, "PDF download logic here.");
    }

    public void handleRequestJoin(int row) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_platform", "root", "root789")) {
            String title = (String) tableModel.getValueAt(row, 0);
            PreparedStatement stmt = conn.prepareStatement("SELECT project_id FROM projects WHERE title = ?");
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int projectId = rs.getInt("project_id");
                int userId = loggedInUser.getUserId();

                // Check if already requested
                PreparedStatement check = conn.prepareStatement(
                    "SELECT * FROM join_requests WHERE user_id = ? AND project_id = ?"
                );
                check.setInt(1, userId);
                check.setInt(2, projectId);
                ResultSet checkRs = check.executeQuery();

                if (checkRs.next()) {
                    JOptionPane.showMessageDialog(this, "You’ve already requested to join this project.");
                    return;
                }

                // Insert join request
                PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO join_requests (user_id, project_id, status) VALUES (?, ?, 'Pending')"
                );
                insert.setInt(1, userId);
                insert.setInt(2, projectId);
                insert.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Request to join project has been sent!");

            } else {
                JOptionPane.showMessageDialog(this, "❌ Project not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "⚠️ Error while sending join request.");
        }
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private BrowseProjectsPage parent;
    private int selectedRow;
    private String action;

    public ButtonEditor(JCheckBox checkBox, BrowseProjectsPage parent, String action) {
        super(checkBox);
        this.parent = parent;
        this.action = action;
        button = new JButton();
        button.setOpaque(true);

        button.addActionListener(e -> {
            fireEditingStopped();
            if (action.equals("download")) {
                parent.handleDownloadPDF(selectedRow);
            } else if (action.equals("join")) {
                parent.handleRequestJoin(selectedRow);
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        selectedRow = row;
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        return label;
    }
}
