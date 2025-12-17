package io.github.e_psi_lon.wordcrafter;

import io.github.e_psi_lon.wordcrafter.service.ServiceFactory;
import io.github.e_psi_lon.wordcrafter.ui.MainMenuFrame;

import javax.swing.*;

/**
 * Main entry point for the WordCrafter application.
 */
public class WordCrafterApp {
    public static void main(String[] args) {
        ServiceFactory serviceFactory = ServiceFactory.initialize();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainMenuFrame mainMenu = new MainMenuFrame(serviceFactory);
            mainMenu.setVisible(true);
        });
    }
}

