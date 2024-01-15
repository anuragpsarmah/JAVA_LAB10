import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FitnessApp {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fitness_app";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Reverence787!";

    private static JFrame frame;
    private static JTextField usernameField;
    private static JPasswordField passwordField;

    private static JTextField heightField;
    private static JTextField weightField;
    private static JTextField bmiField;
    private static JTextArea currentRecommendationArea;
    private static JTextArea previousRecommendationsArea;

    private static final int PADDING = 10;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Fitness App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createLoginRegisterPanel();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createLoginRegisterPanel() {
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalGlue());

        JLabel titleLabel = new JLabel("Fitness App");
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        loginPanel.add(createPaddedPanel(new JLabel("Username:"), PADDING));
        loginPanel.add(createPaddedPanel(usernameField, PADDING));
        loginPanel.add(createPaddedPanel(new JLabel("Password:"), PADDING));
        loginPanel.add(createPaddedPanel(passwordField, PADDING));
        loginPanel.add(createPaddedPanel(loginButton, PADDING));
        loginPanel.add(createPaddedPanel(registerButton, PADDING));

        loginPanel.add(Box.createVerticalGlue());

        panel.add(loginPanel);
    }

    private static JPanel createPaddedPanel(JComponent component, int padding) {
        JPanel paddedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, padding, padding));
        paddedPanel.add(component);
        return paddedPanel;
    }

    private static void createProfilePanel() {
    JPanel profilePanel = new JPanel();
    frame.getContentPane().add(profilePanel);
    profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));

    heightField = new JTextField(10);
    weightField = new JTextField(10);
    bmiField = new JTextField(10);
    bmiField.setEditable(false);
    currentRecommendationArea = new JTextArea(3, 20);
    currentRecommendationArea.setEditable(false);
    previousRecommendationsArea = new JTextArea(10, 20);
    previousRecommendationsArea.setEditable(false);

    JButton calculateBMIButton = new JButton("Calculate BMI");
    calculateBMIButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            calculateBMI();
        }
    });

    JButton logoutButton = new JButton("Logout");
    logoutButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            logout();
        }
    });

    profilePanel.add(createPaddedPanel(new JLabel("Height (cm):"), PADDING));
    profilePanel.add(createPaddedPanel(heightField, PADDING));
    profilePanel.add(createPaddedPanel(new JLabel("Weight (kg):"), PADDING));
    profilePanel.add(createPaddedPanel(weightField, PADDING));
    profilePanel.add(createPaddedPanel(calculateBMIButton, PADDING));
    profilePanel.add(createPaddedPanel(new JLabel("BMI:"), PADDING));
    profilePanel.add(createPaddedPanel(bmiField, PADDING));
    profilePanel.add(createPaddedPanel(new JLabel("Current Recommendation:"), PADDING));
    profilePanel.add(createPaddedPanel(new JScrollPane(currentRecommendationArea), PADDING));
    profilePanel.add(createPaddedPanel(new JLabel("Previous Recommendations:"), PADDING));
    profilePanel.add(createPaddedPanel(new JScrollPane(previousRecommendationsArea), PADDING));
    profilePanel.add(createPaddedPanel(logoutButton, PADDING));

    updatePreviousRecommendations(usernameField.getText()); // Move this line here

    profilePanel.add(Box.createVerticalGlue());

    frame.getContentPane().removeAll();
    frame.getContentPane().add(profilePanel);
    frame.pack();
    frame.repaint();
}

    private static void updatePreviousRecommendations(String username) {
    try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
        String selectQuery = "SELECT recommendation FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                StringBuilder previousRecommendations = new StringBuilder();
                while (resultSet.next()) {
                    previousRecommendations.append(resultSet.getString("recommendation")).append("\n");
                }
                previousRecommendationsArea.setText(previousRecommendations.toString());
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private static void calculateBMI() {
        try {
            double height = Double.parseDouble(heightField.getText());
            double weight = Double.parseDouble(weightField.getText());

            double bmi = calculateBMIValue(height, weight);
            bmiField.setText(String.format("%.2f", bmi));

            String currentRecommendation = getRecommendation(bmi);
            currentRecommendationArea.setText(currentRecommendation);

            String previousRecommendations = previousRecommendationsArea.getText();
            previousRecommendations += currentRecommendation + "\n";
            previousRecommendationsArea.setText(previousRecommendations);

            storeRecommendation(usernameField.getText(), currentRecommendation);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid height and weight", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static double calculateBMIValue(double height, double weight) {
        return weight / ((height / 100) * (height / 100));
    }

    private static String getRecommendation(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private static void storeRecommendation(String username, String recommendation) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String updateQuery = "UPDATE users SET recommendation = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, recommendation);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (validateLogin(username, password)) {
            updatePreviousRecommendations(username); 
            createProfilePanel();
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private static boolean validateLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void logout() {
    String username = usernameField.getText();
    frame.getContentPane().removeAll();
    createLoginRegisterPanel();
    frame.pack();
    frame.repaint();
    updatePreviousRecommendations(username);
}

    private static void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (isUsernameAvailable(username)) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Registration successful", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        createProfilePanel();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Registration failed", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Username already exists. Please choose another username.",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean isUsernameAvailable(String username) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return !resultSet.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
