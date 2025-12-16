package io.github.e_psi_lon.wordcrafter;

import io.github.e_psi_lon.wordcrafter.service.ServiceFactory;
import io.github.e_psi_lon.wordcrafter.ui.MainMenuFrame;

import javax.swing.*;

/**
 * Main entry point for the WordCrafter application.
 */
public class WordCrafterApp {
    public static void main(String[] args) {
        // Initialize services and database
        ServiceFactory serviceFactory = ServiceFactory.initialize();

        // Set look and feel for better UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch the main menu with dependency injection
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame mainMenu = new MainMenuFrame(serviceFactory);
            mainMenu.setVisible(true);
        });
    }
}

