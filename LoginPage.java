// >java -cp ".;resources/.;mysql-connector-j-9.3.0.jar" LoginPage

// C:\Users\jjuma>cd C:\Users\jjuma\OneDrive\Desktop\college\4th SEM\JAVA\student_project_platform

//C:\Users\jjuma\OneDrive\Desktop\college\4th SEM\JAVA\student_project_platform>java -cp ".;resources/.;mysql-connector-j-9.3.0.jar" LoginPage

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

// Custom JPanel to draw the background image
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(Image image) {
        this.backgroundImage = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class LoginPage extends JFrame implements ActionListener {

    private JComboBox<String> roleComboBox;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JLabel messageLabel;
    private UserDAO userDAO;
    private Image backgroundImage;

    public LoginPage() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userDAO = new UserDAO();

        // Load background image
        java.net.URL imageUrl = getClass().getResource("/resources/bg.jpg");
        System.out.println("Image URL: " + imageUrl);
        if (imageUrl == null) {
            System.out.println("Image resource not found!");
        }
        try {
            backgroundImage = ImageIO.read(imageUrl);
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            // Handle the error appropriately, e.g., use a default background color
            backgroundImage = null;
        }

        // Create a BackgroundPanel
        BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImage);
        backgroundPanel.setLayout(new GridBagLayout()); // Set layout for the panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Ensure components fill horizontal space

        JLabel titleLabel = new JLabel("Student Project Collaboration Platform");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;

        // Login As
        gbc.gridy++;
        addLabel(backgroundPanel, "Login As:", gbc);
        addComboBox(backgroundPanel, new String[]{"Student", "Admin"}, roleComboBox = new JComboBox<>(), gbc);

        // Email
        gbc.gridy++;
        addLabel(backgroundPanel, "Email:", gbc);
        addTextField(backgroundPanel, emailField = new JTextField(20), gbc);

        // Password
        gbc.gridy++;
        addLabel(backgroundPanel, "Password:", gbc);
        addPasswordField(backgroundPanel, passwordField = new JPasswordField(20), gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);

        signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(100, 35));
        signUpButton.addActionListener(this);
        buttonPanel.add(signUpButton);
        backgroundPanel.add(buttonPanel, gbc);

        // Message Label
        gbc.gridy++;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        backgroundPanel.add(messageLabel, gbc);

        // Set the content pane of the JFrame to our custom JPanel
        setContentPane(backgroundPanel);

        setPreferredSize(new Dimension(500, 450));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper methods to add components with GridBagConstraints
    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(text, SwingConstants.RIGHT), gbc);
    }

    private void addComboBox(JPanel panel, String[] items, JComboBox<String> comboBox, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(150, 30));
        panel.add(comboBox, gbc);
        this.roleComboBox = comboBox; // Assign to the class member
    }

    private void addTextField(JPanel panel, JTextField textField, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        textField.setPreferredSize(new Dimension(150, 30));
        panel.add(textField, gbc);
        this.emailField = textField; // Assign to the class member
    }

    private void addPasswordField(JPanel panel, JPasswordField passwordField, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField.setPreferredSize(new Dimension(150, 30));
        panel.add(passwordField, gbc);
        this.passwordField = passwordField; // Assign to the class member
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String role = (String) roleComboBox.getSelectedItem();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            User loggedInUser = userDAO.loginUser(role, email, password); // INSECURE: Plain text comparison!

            if (loggedInUser != null) {
                messageLabel.setForeground(new Color(0, 128, 0));
                messageLabel.setText("Login successful!");
                SwingUtilities.invokeLater(() -> new MainPage(loggedInUser));
                dispose();
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Login failed. Invalid credentials.");
            }
        } else if (e.getSource() == signUpButton) {
            SwingUtilities.invokeLater(SignUpForm::new);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(LoginPage::new);
    }
}