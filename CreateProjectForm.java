import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CreateProjectForm extends JFrame implements ActionListener {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JFileChooser fileChooser;
    private JButton uploadButton, postButton;
    private JLabel selectedFileLabel;
    private ProjectDAO projectDAO;
    private User loggedInUser;
    private File selectedPdfFile;

    public CreateProjectForm(User user) {
        setTitle("Post New Project");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));
        setPreferredSize(new Dimension(500, 200)); // Adjusted size

        loggedInUser = user;
        projectDAO = new ProjectDAO();
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));

        add(new JLabel("Project Title:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Short Description:"));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        add(new JScrollPane(descriptionArea));

        add(new JLabel("Detailed Project Idea (PDF):"));
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        uploadButton = new JButton("Upload PDF");
        uploadButton.addActionListener(this);
        filePanel.add(uploadButton);
        selectedFileLabel = new JLabel("No file selected");
        filePanel.add(selectedFileLabel);
        add(filePanel);

        postButton = new JButton("Post Project");
        postButton.addActionListener(this);
        add(postButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedPdfFile = fileChooser.getSelectedFile();
                selectedFileLabel.setText("Selected: " + selectedPdfFile.getName());
            }
        } else if (e.getSource() == postButton) {
            String title = titleField.getText();
            String shortDescription = descriptionArea.getText();

            if (title.isEmpty() || shortDescription.isEmpty() || selectedPdfFile == null) {
                JOptionPane.showMessageDialog(this, "Please fill title, description, and upload a PDF.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (FileInputStream fis = new FileInputStream(selectedPdfFile)) {
                if (projectDAO.postProject(loggedInUser.getUserId(), title, shortDescription, fis, selectedPdfFile.getName())) {
                    JOptionPane.showMessageDialog(this, "Project posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close the form after posting
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to post project.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading PDF file.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // public static void main(String[] args) { // For testing
    //     User dummyUser = new User();
    //     dummyUser.setUserId(1);
    //     SwingUtilities.invokeLater(() -> new CreateProjectForm(dummyUser));
    // }
}