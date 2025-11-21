import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfileUpdatePage extends JFrame implements ActionListener {

    private JLabel idLabel, roleLabel;
    private JTextField nameField, emailField, yearField, skillsField;
    private JPasswordField newPasswordField, confirmPasswordField; // For changing password
    private JComboBox<String> departmentComboBox;
    private JButton updateButton, changePasswordButton, saveNewPasswordButton; // Added save password button
    private JLabel messageLabel;
    private User loggedInUser;
    private UserDAO userDAO;
    private JPanel passwordPanel; // Panel to show/hide password fields

    public ProfileUpdatePage(User user) {
        setTitle("Profile Update");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(11, 2, 10, 10)); // Increased rows for new password fields
        setPreferredSize(new Dimension(450, 400)); // Increased height

        loggedInUser = user;
        userDAO = new UserDAO();

        add(new JLabel("User ID:"));
        idLabel = new JLabel(String.valueOf(loggedInUser.getUserId()));
        add(idLabel);

        add(new JLabel("Role:"));
        roleLabel = new JLabel(loggedInUser.getRole());
        add(roleLabel);

        add(new JLabel("Name:"));
        nameField = new JTextField(loggedInUser.getName());
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField(loggedInUser.getEmail());
        add(emailField);

        add(new JLabel("Department:"));
        String[] departments = {"Computer Science", "Electrical Engineering", "Mechanical Engineering", "Other"};
        departmentComboBox = new JComboBox<>(departments);
        departmentComboBox.setSelectedItem(loggedInUser.getDepartment());
        add(departmentComboBox);

        add(new JLabel("Year:"));
        yearField = new JTextField(loggedInUser.getYear());
        add(yearField);

        add(new JLabel("Skills (comma-separated):"));
        skillsField = new JTextField(loggedInUser.getSkills());
        add(skillsField);

        updateButton = new JButton("Update Profile");
        updateButton.addActionListener(this);
        add(updateButton);

        changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(this);
        add(changePasswordButton);

        // Panel for new password fields (initially hidden)
        passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newPasswordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        saveNewPasswordButton = new JButton("Save New Password");
        saveNewPasswordButton.addActionListener(this);
        passwordPanel.add(new JLabel("New Password:"));
        passwordPanel.add(newPasswordField);
        passwordPanel.add(new JLabel("Confirm Password:"));
        passwordPanel.add(confirmPasswordField);
        passwordPanel.add(saveNewPasswordButton);
        passwordPanel.setVisible(false); // Initially hide the password panel
        add(passwordPanel);
        add(new JLabel("")); // For spacing

        messageLabel = new JLabel("");
        add(messageLabel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateButton) {
            String name = nameField.getText();
            String email = emailField.getText();
            String department = (String) departmentComboBox.getSelectedItem();
            String year = yearField.getText();
            String skills = skillsField.getText();

            boolean updated = userDAO.updateUserProfile(loggedInUser.getUserId(), name, email, department, year, skills);
            if (updated) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Profile updated successfully!");
                // Update the loggedInUser object with the new data
                loggedInUser.setName(name);
                loggedInUser.setEmail(email);
                loggedInUser.setDepartment(department);
                loggedInUser.setYear(year);
                loggedInUser.setSkills(skills);
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Failed to update profile.");
            }
        } else if (e.getSource() == changePasswordButton) {
            // Show the panel for changing password
            passwordPanel.setVisible(true);
            pack(); // Resize the frame to accommodate the new panel
        } else if (e.getSource() == saveNewPasswordButton) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Please enter and confirm the new password.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("New passwords do not match.");
                return;
            }

            // INSECURE: Password should be hashed before saving!
            if (userDAO.updateUserPassword(loggedInUser.getUserId(), newPassword)) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Password updated successfully!");
                passwordPanel.setVisible(false); // Hide the password panel after saving
                pack(); // Resize the frame
                // Clear the password fields
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Failed to update password.");
            }
        }
    }

    // You might want to add a main method for testing this form directly
    // public static void main(String[] args) {
    //     // Create a dummy user for testing
    //     User testUser = new User();
    //     testUser.setUserId(1);
    //     testUser.setRole("Student");
    //     testUser.setName("Test User");
    //     testUser.setEmail("test@example.com");
    //     testUser.setDepartment("Computer Science");
    //     testUser.setYear("2023");
    //     testUser.setSkills("Java, Python");
    //     SwingUtilities.invokeLater(() -> new ProfileUpdatePage(testUser));
    // }
}