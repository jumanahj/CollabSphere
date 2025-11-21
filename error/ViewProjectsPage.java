import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor; // Ensure this import is present
import javax.swing.table.TableCellRenderer; // Ensure this import is present
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ViewProjectsPage extends JFrame implements ActionListener {

    private JTable projectsTable;
    private DefaultTableModel tableModel;
    private ProjectDAO projectDAO;
    private UserDAO userDAO; // To fetch user email
    private List<Project> projectsList;
    private User loggedInUser;

    private static final String EMAIL_USERNAME = "your_email@example.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "your_password";       // Replace with your password

    public ViewProjectsPage(User user) {
		 loggedInUser = user;
        setTitle("Browse Projects");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 600)); // Increased width

        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
        projectsList = projectDAO.getAllProjects();

        String[] columnNames = {"ID", "Title", "Description", "PDF File", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the "Actions" column non-editable
                return column != 4;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 4) ? JButton.class : super.getColumnClass(column);
            }
        };
        projectsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(projectsTable);

        // Add action buttons to the table
        ActionRenderer actionRenderer = new ActionRenderer();
        projectsTable.getColumnModel().getColumn(4).setCellRenderer(actionRenderer);
        ActionEditor actionEditor = new ActionEditor(this);
        projectsTable.getColumnModel().getColumn(4).setCellEditor(actionEditor);

        add(scrollPane, BorderLayout.CENTER);

        populateTable();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Project project : projectsList) {
            JButton downloadButton = new JButton("Download PDF");
            downloadButton.setActionCommand("download_" + project.getProjectId());
            downloadButton.addActionListener(this);

            JButton requestJoinButton = new JButton("Request Join");
            requestJoinButton.setActionCommand("join_" + project.getProjectId());
            requestJoinButton.addActionListener(this);

            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            actionsPanel.add(downloadButton);
            actionsPanel.add(requestJoinButton);

            Object[] rowData = {project.getProjectId(), project.getTitle(), project.getShortDescription(),
                                project.getPdfFilename() != null ? "Available" : "N/A", actionsPanel};
            tableModel.addRow(rowData);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.startsWith("download_")) {
            int projectId = Integer.parseInt(command.substring("download_".length()));
            downloadPdf(projectId);
        } else if (command.startsWith("join_")) {
            int projectId = Integer.parseInt(command.substring("join_".length()));
            requestJoin(projectId);
        }
    }

    private void downloadPdf(int projectId) {
        byte[] pdfData = projectDAO.getProjectPdf(projectId);
        if (pdfData != null) {
            Project selectedProject = null;
            for (Project p : projectsList) {
                if (p.getProjectId() == projectId) {
                    selectedProject = p;
                    break;
                }
            }
            if (selectedProject != null && selectedProject.getPdfFilename() != null) {
                try {
                    File outputFile = new File(selectedProject.getPdfFilename());
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    fos.write(pdfData);
                    fos.close();
                    JOptionPane.showMessageDialog(this, "PDF downloaded as: " + outputFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    Desktop.getDesktop().open(outputFile);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error downloading PDF.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "PDF file not found for this project.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error retrieving PDF data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   private void requestJoin(int projectId) {
       Project selectedProject = null;
       for (Project p : projectsList) {
           if (p.getProjectId() == projectId) {
               selectedProject = p;
               break;
           }
       }

       if (selectedProject != null) {
           User projectOwner = userDAO.getUserById(selectedProject.getUserId());
           if (projectOwner != null && projectOwner.getEmail() != null) {
               String recipientEmail = projectOwner.getEmail();
               String subject = "Request to Join Project: " + selectedProject.getTitle();
               String requesterName = loggedInUser.getName(); // Get requester's name
               String requesterEmail = loggedInUser.getEmail(); // Get requester's email
               String body = "Hello " + projectOwner.getName() + ",\n\n" +
                             requesterName + " (" + requesterEmail + ") is interested in joining your project: " + selectedProject.getTitle() + " (ID: " + selectedProject.getProjectId() + ").\n\n" +
                             "Please contact them if you are interested in having them on your team.\n\n" +
                             "Sincerely,\n" +
                             "Student Collaboration Platform";

               sendEmail(recipientEmail, subject, body, loggedInUser.getEmail()); // Use loggedInUser's email as sender
               JOptionPane.showMessageDialog(this, "Join request sent to " + projectOwner.getName() + ".", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
           } else {
               JOptionPane.showMessageDialog(this, "Could not find the project owner's email.", "Error", JOptionPane.ERROR_MESSAGE);
           }
       } else {
           JOptionPane.showMessageDialog(this, "Error finding the selected project.", "Error", JOptionPane.ERROR_MESSAGE);
       }
}
   private void sendEmail(String toEmail, String subject, String body, String fromEmail) {
           // Replace with your actual email server details
           String host = "smtp.gmail.com";
           String port = "587"; // Or 465 for SSL
           Properties properties = new Properties();
           properties.put("mail.smtp.host", host);
           properties.put("mail.smtp.port", port);
           properties.put("mail.smtp.auth", "true");
           properties.put("mail.smtp.starttls.enable", "true"); // Use TLS

           Session session = Session.getInstance(properties, new Authenticator() {
               @Override
               protected PasswordAuthentication getPasswordAuthentication() {
                   return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
               }
           });

           try {
               Message message = new MimeMessage(session);
               message.setFrom(new InternetAddress(fromEmail));
               message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(toEmail)}); // Updated
               message.setSubject(subject);
               message.setText(body);

               Transport.send(message);
               System.out.println("Email sent successfully from " + fromEmail + " to: " + toEmail);
           } catch (MessagingException e) {
               System.err.println("Error sending email: " + e.getMessage());
               JOptionPane.showMessageDialog(this, "Error sending join request email.", "Email Error", JOptionPane.ERROR_MESSAGE);
           }
    }
    // Helper classes to render and edit buttons in the table
    class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JPanel panel;
        private JButton downloadButton;
        private JButton requestJoinButton;
        private int projectId;
        private ViewProjectsPage parent;

        public ActionEditor(ViewProjectsPage parent) {
            this.parent = parent;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            downloadButton = new JButton("Download PDF");
            requestJoinButton = new JButton("Request Join");
            downloadButton.addActionListener(this);
            requestJoinButton.addActionListener(this);
            panel.add(downloadButton);
            panel.add(requestJoinButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            projectId = (int) table.getModel().getValueAt(row, 0);
            downloadButton.setActionCommand("download_" + projectId);
            requestJoinButton.setActionCommand("join_" + projectId);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null; // Not editing a value
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            parent.actionPerformed(e);
            fireEditingStopped();
        }
    }

    // public static void main(String[] args) { // For testing
    //     SwingUtilities.invokeLater(ViewProjectsPage::new);
    // }
}