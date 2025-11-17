package io.github.e_psi_lon.wordcrafter.ui.game;

import javax.swing.*;
import java.awt.*;

/**
 * Free build mode frame - SPOOFED/STUB implementation.
 * This mode is not yet fully implemented.
 */
public class FreeBuildFrame extends JFrame {
    private static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    
    public FreeBuildFrame() {
        setTitle("WordCrafter - Mode construction libre");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel messageLabel = new JLabel("<html><center><h1>Mode construction libre</h1><br><br>" +
                                        "Ce mode de jeu n'est pas encore implémenté.<br><br>" +
                                        "Prochainement !</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(new Color(199, 21, 133));
        
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
}

