package io.github.e_psi_lon.wordcrafter.ui;

import io.github.e_psi_lon.wordcrafter.model.Admin;
import io.github.e_psi_lon.wordcrafter.model.Player;
import io.github.e_psi_lon.wordcrafter.model.User;
import io.github.e_psi_lon.wordcrafter.ui.game.MainGameFrame;
import io.github.e_psi_lon.wordcrafter.ui.game.FreeBuildFrame;
import io.github.e_psi_lon.wordcrafter.ui.game.PrefixMatcherFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main menu frame with pastel pink cloud-y theme.
 */
public class MainMenuFrame extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private JLabel userLabel;
    private JButton editorButton;
    private JPanel glassPanelOverlay;

    // Pastel pink theme colors
    private static final Color PASTEL_PINK = new Color(255, 209, 220);
    private static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    private static final Color BUTTON_COLOR = new Color(255, 182, 193);

    public MainMenuFrame() {
        setTitle("WordCrafter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        initComponents();
        applyTheme();
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title
        JLabel titleLabel = new JLabel("WordCrafter");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(199, 21, 133)); // Medium violet red
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // User info label
        userLabel = new JLabel("Non connecté");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Game mode buttons
        JButton mainGameButton = createStyledButton("Mode de Jeu Principal");
        mainGameButton.addActionListener(e -> launchMainGame());
        mainPanel.add(mainGameButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton freeBuildButton = createStyledButton("Mode Construction Libre");
        freeBuildButton.addActionListener(e -> launchFreeBuild());
        mainPanel.add(freeBuildButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton prefixMatcherButton = createStyledButton("Mode Prefix Matcher");
        prefixMatcherButton.addActionListener(e -> launchPrefixMatcher());
        mainPanel.add(prefixMatcherButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Editor button (only visible for admins)
        editorButton = createStyledButton("Mode éditeur");
        editorButton.addActionListener(e -> launchEditor());
        editorButton.setVisible(false);
        mainPanel.add(editorButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Other buttons
        JButton settingsButton = createStyledButton("Paramètres");
        settingsButton.addActionListener(e -> showSettings());
        mainPanel.add(settingsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton loginButton = createStyledButton("Connection / Compte");
        loginButton.addActionListener(e -> showLogin());
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton quitButton = createStyledButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(quitButton);

        add(mainPanel);

        // Setup glass pane overlay for frozen state
        setupGlassPane();
    }

    private void setupGlassPane() {
        glassPanelOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw semi-transparent gray overlay
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(128, 128, 128, 100)); // Gray with 100/255 alpha (about 40% opacity)
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        glassPanelOverlay.setOpaque(false);
        glassPanelOverlay.setVisible(false);
        setGlassPane(glassPanelOverlay);
    }

    private void showFrozenOverlay() {
        glassPanelOverlay.setVisible(true);
    }

    private void hideFrozenOverlay() {
        glassPanelOverlay.setVisible(false);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 40));
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PASTEL_PINK, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    private void applyTheme() {
        mainPanel.setBackground(LIGHT_CLOUD);
        getContentPane().setBackground(PASTEL_PINK);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    private void updateUI() {
        // Update user label with current user info
        if (currentUser != null) {
            String scoreText = "";
            if (currentUser instanceof Player player) {
                scoreText = " (Score: " + player.getScore() + ")";
            }
            userLabel.setText("Bienvenue, " + currentUser.getUsername() + scoreText);
        } else {
            userLabel.setText("Non connecté");
        }

        // Show/hide editor button based on user role
        editorButton.setVisible(currentUser != null && currentUser instanceof Admin);

        revalidate();
        repaint();
    }
    
    private void launchMainGame() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord vous connecter !", "Connexion requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showFrozenOverlay();

        // Create as a modal dialog instead of a frame
        JDialog gameDialog = new JDialog(this, "WordCrafter - Mode de jeu principal", true);
        MainGameFrame gameFrame = new MainGameFrame(currentUser);
        gameLauncher(gameDialog, gameFrame.getContentPane(), gameFrame.getSize());
    }
    
    private void launchFreeBuild() {
        showFrozenOverlay();

        JDialog freeBuildDialog = new JDialog(this, "WordCrafter - Mode construction libre", true);
        FreeBuildFrame freeBuildFrame = new FreeBuildFrame();
        gameLauncher(freeBuildDialog, freeBuildFrame.getContentPane(), freeBuildFrame.getSize());
    }

    private void gameLauncher(JDialog dialog, Container contentPane, Dimension size) {
        dialog.setContentPane(contentPane);
        dialog.setSize(size);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                hideFrozenOverlay();
            }
        });
        dialog.setVisible(true);
    }

    private void launchPrefixMatcher() {
        showFrozenOverlay();

        JDialog prefixDialog = new JDialog(this, "WordCrafter - Mode préfixe-matcher", true);
        PrefixMatcherFrame prefixMatcherFrame = new PrefixMatcherFrame();
        gameLauncher(prefixDialog, prefixMatcherFrame.getContentPane(), prefixMatcherFrame.getSize());
    }
    
    private void launchEditor() {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            JOptionPane.showMessageDialog(this, "Accès administrateur requis !", "Accès refusé", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showFrozenOverlay();

        JDialog editorDialog = new JDialog(this, "WordCrafter - Mode Éditeur", true);
        EditorFrame editorFrame = new EditorFrame();
        gameLauncher(editorDialog, editorFrame.getContentPane(), editorFrame.getSize());
    }
    
    private void showSettings() {
        JOptionPane.showMessageDialog(this, "Paramètres pas encore implémentés.", "Paramètres", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLogin() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.getAuthenticatedUser() != null) {
            setCurrentUser(loginDialog.getAuthenticatedUser());
        }
    }
}


