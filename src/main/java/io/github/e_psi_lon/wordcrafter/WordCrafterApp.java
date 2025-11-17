package io.github.e_psi_lon.wordcrafter;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.ui.MainMenuFrame;

import javax.swing.*;

/**
 * Main entry point for the WordCrafter application.
 */
public class WordCrafterApp {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager.getInstance().initialize();

        // Set look and feel for better UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch the main menu
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame mainMenu = new MainMenuFrame();
            mainMenu.setVisible(true);
        });
    }
}

