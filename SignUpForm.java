import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpForm extends JFrame implements ActionListener {

    private JTextField nameField, emailField, skillsField;
    private JPasswordField passwordField;
    private JComboBox<String> departmentComboBox;
    private JRadioButton year1, year2, year3, year4, year5;
    private ButtonGroup yearGroup;
    private JButton signUpButton;
    private JLabel messageLabel;
    private UserDAO userDAO;

    public SignUpForm() {
        setTitle("Student Sign Up Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        // Colors
        Color bgColor = new Color(245, 250, 255);
        Color btnColor = new Color(100, 149, 237); // Cornflower Blue
        Color textColor = new Color(30, 30, 30);

        getContentPane().setBackground(bgColor);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        userDAO = new UserDAO();

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Department (Dropdown)
        gbc.gridx = 0; gbc.gridy++;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(labelFont);
        add(deptLabel, gbc);

        String[] departments = {"Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil", "Other"};
        departmentComboBox = new JComboBox<>(departments);
        departmentComboBox.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        add(departmentComboBox, gbc);

        // Year (Radio Buttons)
        gbc.gridx = 0; gbc.gridy++;
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(labelFont);
        add(yearLabel, gbc);

        JPanel yearPanel = new JPanel();
        yearPanel.setBackground(bgColor);
        year1 = new JRadioButton("1st");
        year2 = new JRadioButton("2nd");
        year3 = new JRadioButton("3rd");
        year4 = new JRadioButton("4th");
        year5 = new JRadioButton("5th");

        yearGroup = new ButtonGroup();
        yearGroup.add(year1); yearGroup.add(year2); yearGroup.add(year3); yearGroup.add(year4); yearGroup.add(year5);

        yearPanel.add(year1); yearPanel.add(year2); yearPanel.add(year3); yearPanel.add(year4); yearPanel.add(year5);

        gbc.gridx = 1;
        add(yearPanel, gbc);

        // Skills
        gbc.gridx = 0; gbc.gridy++;
        JLabel skillsLabel = new JLabel("Skills:");
        skillsLabel.setFont(labelFont);
        add(skillsLabel, gbc);

        skillsField = new JTextField();
        skillsField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        add(skillsField, gbc);

        // Button
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(btnColor);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setPreferredSize(new Dimension(120, 35));
        signUpButton.addActionListener(this);
        add(signUpButton, gbc);

        // Message
        gbc.gridy++;
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signUpButton) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String department = (String) departmentComboBox.getSelectedItem();
            String year = "";

            if (year1.isSelected()) year = "1";
            else if (year2.isSelected()) year = "2";
            else if (year3.isSelected()) year = "3";
            else if (year4.isSelected()) year = "4";
            else if (year5.isSelected()) year = "5";

            String skills = skillsField.getText().trim();
            String role = "Student"; // Default role

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || year.isEmpty()) {
                messageLabel.setText("Please fill all required fields.");
                return;
            }

            if (!email.contains("@")) {
                messageLabel.setText("Invalid email format.");
                return;
            }

            // INSECURE: Password should be hashed
            if (userDAO.registerUser(role, name, email, password, department, year, skills)) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Registration successful!");
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Registration failed. Email might already exist.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignUpForm::new);
    }
}
