import managers.SessionManager;
import ui.MainFrame;
import utils.ThemeManager;

import javax.swing.*;
import java.io.File;
/**
 * AlgoVerse — Entry Point
 * Run: java -cp out Main   (after compiling with run.bat)
 */
public class Main {
    public static void main(String[] args) {
        // 1. Ensure data directory exists
        new File("data").mkdirs();
        new File("data/users.txt").getParentFile().mkdirs();
        new File("data/scores.txt").getParentFile().mkdirs();

        // 2. Apply dark theme before any window is created
        ThemeManager.applyDarkTheme();

        // 3. Launch on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Try to restore remembered session
            SessionManager.getInstance().loadRememberedSession();

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
