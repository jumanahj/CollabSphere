import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {

    private User loggedInUser;
    private JLabel welcomeLabel;

    public MainPage(User user) {
        setTitle("Student Collaboration Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        loggedInUser = user;

        // Welcome Label
        welcomeLabel = new JLabel("Welcome, " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        // Center Panel for other operations
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton viewProfileButton = new JButton("View Profile");
        viewProfileButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ProfileUpdatePage(loggedInUser));
        });
        centerPanel.add(viewProfileButton);

        JButton postProjectButton = new JButton("Post Project Idea");
        postProjectButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new CreateProjectForm(loggedInUser));
        });
        centerPanel.add(postProjectButton);

        JButton viewProjectsButton = new JButton("Browse Projects");
		viewProjectsButton.addActionListener(e -> {
		    SwingUtilities.invokeLater(() -> new BrowseProjectsPage(loggedInUser)); // Pass loggedInUser
		});
centerPanel.add(viewProjectsButton);

        add(centerPanel, BorderLayout.CENTER);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            SwingUtilities.invokeLater(LoginPage::new);
            dispose();
        });
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}