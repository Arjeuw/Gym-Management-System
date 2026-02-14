import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run the GUI (Nimbus Look and Feel)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Start the Application
        SwingUtilities.invokeLater(() -> new GymGUI());
    }
}