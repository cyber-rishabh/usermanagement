import java.sql.*; // Import all classes from java.sql for JDBC operations
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // H2 Database URL:
    // jdbc:h2:~/testdb -> A file-based database named 'testdb' in your user's home directory.
    //                     It will be created if it doesn't exist.
    // jdbc:h2:mem:testdb -> An in-memory database that is cleared when the application closes.
    private static final String DB_URL = "jdbc:h2:~/UserDB"; // Let's use 'UserDB' for this project
    private static final String DB_USER = "sa"; // Default H2 username
    private static final String DB_PASSWORD = ""; // Default H2 password (blank)

    /**
     * Establishes a connection to the H2 database.
     * @return A Connection object.
     * @throws SQLException If a database access error occurs.
     */
    private Connection getConnection() throws SQLException {
        // This line ensures the H2 driver is loaded. While often implicitly loaded in modern Java,
        // it's good practice for clarity or older environments.
        // Class.forName("org.h2.Driver"); // No longer strictly needed with newer JDBC, but harmless.
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Ensures the 'users' table exists. Creates it if it doesn't.
     */
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL" + // Email must be unique
                ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table 'users' ensured to exist in " + DB_URL);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new user into the database (CREATE operation).
     * @param user The User object containing name and email.
     */
    public void addUser(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1)); // Set the generated ID back to the user object
                    }
                }
                System.out.println("User added: " + user.getName() + " with ID: " + user.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all users from the database (READ operation).
     * @return A list of User objects.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Retrieves a single user by ID from the database (READ operation).
     * @param id The ID of the user to retrieve.
     * @return The User object if found, otherwise null.
     */
    public User getUserById(int id) {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // User not found
    }

    /**
     * Updates an existing user in the database (UPDATE operation).
     * @param user The User object with updated name/email and existing ID.
     */
    public void updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User updated: " + user.getName() + " (ID: " + user.getId() + ")");
            } else {
                System.out.println("User with ID " + user.getId() + " not found for update.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user from the database (DELETE operation).
     * @param id The ID of the user to delete.
     */
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User deleted with ID: " + id);
            } else {
                System.out.println("User with ID " + id + " not found for deletion.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}