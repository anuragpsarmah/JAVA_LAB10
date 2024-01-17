import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class FitnessCRUDAppGUI extends JFrame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fitness_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Reverence787!";

    public FitnessCRUDAppGUI() {
        setTitle("Fitness Information Management");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            createTable(connection);

            // Set up the initial display
            displayFitnessInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JButton addButton = new JButton("Add Fitness Information");
        addButton.setBounds(10, 20, 200, 25);
        panel.add(addButton);

        JButton displayButton = new JButton("Display Fitness Information");
        displayButton.setBounds(10, 50, 200, 25);
        panel.add(displayButton);

        JButton updateButton = new JButton("Update Fitness Information");
        updateButton.setBounds(10, 80, 200, 25);
        panel.add(updateButton);

        JButton deleteButton = new JButton("Delete Fitness Information");
        deleteButton.setBounds(10, 110, 200, 25);
        panel.add(deleteButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(220, 110, 80, 25);
        panel.add(exitButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFitnessInfo();
            }
        });

        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayFitnessInfo();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFitnessInfo();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFitnessInfo();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void addFitnessInfo() {
        String name = JOptionPane.showInputDialog("Enter name:");
        int age = Integer.parseInt(JOptionPane.showInputDialog("Enter age:"));
        double weight = Double.parseDouble(JOptionPane.showInputDialog("Enter weight:"));
        double height = Double.parseDouble(JOptionPane.showInputDialog("Enter height:"));

        String insertSQL = "INSERT INTO fitness_info (name, age, weight, height) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setDouble(3, weight);
            preparedStatement.setDouble(4, height);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Fitness information added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add fitness information.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void displayFitnessInfo() {
        String selectSQL = "SELECT * FROM fitness_info";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectSQL)) {

            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                double weight = resultSet.getDouble("weight");
                double height = resultSet.getDouble("height");

                result.append("ID: ").append(id).append(", Name: ").append(name).append(", Age: ").append(age)
                        .append(", Weight: ").append(weight).append(", Height: ").append(height).append("\n");
            }

            JOptionPane.showMessageDialog(this, result.toString(), "Fitness Information",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateFitnessInfo() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Enter the ID of the fitness information to update:"));
        double newWeight = Double.parseDouble(JOptionPane.showInputDialog("Enter new weight:"));

        String updateSQL = "UPDATE fitness_info SET weight = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            preparedStatement.setDouble(1, newWeight);
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Fitness information updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update fitness information. ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteFitnessInfo() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Enter the ID of the fitness information to delete:"));

        String deleteSQL = "DELETE FROM fitness_info WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Fitness information deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete fitness information. ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS fitness_info (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "age INT," +
                "weight DOUBLE," +
                "height DOUBLE)";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FitnessCRUDAppGUI().setVisible(true);
            }
        });
    }
}
