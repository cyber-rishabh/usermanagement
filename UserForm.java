import javax.swing.*;
import javax.swing.table.DefaultTableModel; // For managing data in the JTable
import java.awt.*;
import java.awt.event.MouseAdapter; // For listening to mouse clicks on the table
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector; // Used by DefaultTableModel

public class UserForm extends JFrame {
    private DatabaseManager dbManager; // Instance of our database manager
    private JTextField idField, nameField, emailField;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private JTable userTable;
    private DefaultTableModel tableModel; // Model for our JTable

    public UserForm() {
        super("User Management Application");
        dbManager = new DatabaseManager(); // Initialize the database manager
        dbManager.createTable(); // Ensure the 'users' table exists when the app starts

        // --- UI Layout Setup ---
        setLayout(new BorderLayout(10, 10)); // Add some padding

        // 1. Input Panel (North)
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // 4 rows, 2 columns, with gaps
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        inputPanel.add(new JLabel("ID (for Update/Delete):"));
        idField = new JTextField();
        idField.setEditable(false); // ID is usually auto-generated or selected from table
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        inputPanel.add(emailField);

        add(inputPanel, BorderLayout.NORTH);

        // 2. Button Panel (Center)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Buttons centered with gaps
        addButton = new JButton("Add User");
        updateButton = new JButton("Update User");
        deleteButton = new JButton("Delete User");
        refreshButton = new JButton("Refresh List");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.CENTER);

        // 3. User Table (South)
        String[] columnNames = {"ID", "Name", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make table non-editable by default
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row to be selected
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.SOUTH);

        // --- Action Listeners for Buttons ---
        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsersIntoTable());

        // --- Table Selection Listener (to populate fields when a row is clicked) ---
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) { // If a row is actually selected
                    // Get data from the selected row
                    idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });

        // --- Initial Load of Users and Frame Settings ---
        loadUsersIntoTable(); // Populate table when the form first opens

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Adjusts frame size to fit components
        setMinimumSize(new Dimension(600, 450)); // Set a minimum size
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    // --- CRUD Operation Methods connected to UI ---

    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(name, email);
        dbManager.addUser(newUser);
        loadUsersIntoTable(); // Refresh the table
        clearFields(); // Clear input fields
        JOptionPane.showMessageDialog(this, "User added successfully! (ID: " + newUser.getId() + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateUser() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table or enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email cannot be empty for update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User userToUpdate = new User(id, name, email);
            dbManager.updateUser(userToUpdate);
            loadUsersIntoTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please select from table or enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table or enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText());
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user with ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteUser(id);
                loadUsersIntoTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please select from table or enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all rows from the table model and re-populates it with current data from the database.
     */
    private void loadUsersIntoTable() {
        tableModel.setRowCount(0); // Clear existing data in the table
        List<User> users = dbManager.getAllUsers(); // Get all users from DB

        for (User user : users) {
            // Add each user's data as a new row to the table model
            tableModel.addRow(new Object[]{user.getId(), user.getName(), user.getEmail()});
        }
    }

    /**
     * Clears the input text fields.
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
    }
}