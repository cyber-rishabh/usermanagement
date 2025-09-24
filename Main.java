import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensure that GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new UserForm(); // Create and show your UserForm
        });
    }
}