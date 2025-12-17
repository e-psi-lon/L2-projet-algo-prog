package io.github.e_psi_lon.wordcrafter.ui.game;

import io.github.e_psi_lon.wordcrafter.ui.AppColors;

import javax.swing.*;
import java.awt.*;

/**
 * Prefix matcher mode frame - SPOOFED/STUB implementation.
 * This mode is not yet fully implemented.
 */
public class PrefixMatcherFrame extends GameFrame {

    public PrefixMatcherFrame() {
        super(null, null, "WordCrafter - Mode préfixe-matcher");
        setSize(600, 400);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel messageLabel = new JLabel("<html><center><h1>Mode préfixe-matcher</h1><br><br>" +
                                        "Ce mode de jeu n'est pas encore implémenté.<br><br>" +
                                        "Prochainement !</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(AppColors.TITLE_TEXT);

        mainPanel.add(messageLabel, BorderLayout.CENTER);

        add(mainPanel);
    }
}

