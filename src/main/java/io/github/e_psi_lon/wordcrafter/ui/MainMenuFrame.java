package io.github.e_psi_lon.wordcrafter.ui;

import io.github.e_psi_lon.wordcrafter.controller.GameController;
import io.github.e_psi_lon.wordcrafter.model.Admin;
import io.github.e_psi_lon.wordcrafter.model.Player;
import io.github.e_psi_lon.wordcrafter.model.User;
import io.github.e_psi_lon.wordcrafter.service.ServiceFactory;
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
    private JLabel scoreLabel;
    private JLabel welcomeLabel;
    private JButton editorButton;
    private JButton loginButton;
    private JButton disconnectButton;
    private JPanel glassPanelOverlay;
    private final ServiceFactory serviceFactory;

    // Pastel pink theme colors
    private static final Color PASTEL_PINK = new Color(255, 209, 220);
    private static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    private static final Color BUTTON_COLOR = new Color(255, 182, 193);

    public MainMenuFrame(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        setTitle("WordCrafter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        initComponents();
        applyTheme();
    }

    private void initComponents() {
        // Create a top panel with BorderLayout for score (right only)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LIGHT_CLOUD);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Score label on the right
        scoreLabel = new JLabel("");
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(scoreLabel, BorderLayout.EAST);

        // Main content panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Title (centered)
        JLabel titleLabel = new JLabel("WordCrafter");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(199, 21, 133)); // Medium violet red
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Welcome label
        welcomeLabel = new JLabel("Non connecté");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
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

        // Login button (will be replaced with panel when logged in)
        loginButton = createStyledButton("Connexion");
        loginButton.addActionListener(e -> showLogin());
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Disconnect button (created but not added initially)
        disconnectButton = createStyledButton("Déconnecter");
        disconnectButton.setBackground(new Color(220, 100, 100));
        disconnectButton.addActionListener(e -> logout());

        JButton quitButton = createStyledButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(quitButton);

        // Create outer container with BorderLayout
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(LIGHT_CLOUD);
        outerPanel.add(topPanel, BorderLayout.NORTH);
        outerPanel.add(mainPanel, BorderLayout.CENTER);

        add(outerPanel);

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
        // Update score label in top right corner
        if (currentUser != null && currentUser instanceof Player player)
            scoreLabel.setText("Score: " + player.getScore());
        else scoreLabel.setText("");

        if (currentUser != null)
            welcomeLabel.setText("Bienvenue, " + currentUser.getUsername());
        else welcomeLabel.setText("Non connecté");

        if (currentUser != null) {
            loginButton.setText("Compte");
            Component[] components = mainPanel.getComponents();
            int loginButtonIndex = -1;

            for (int i = 0; i < components.length; i++) {
                if (components[i] == loginButton) {
                    loginButtonIndex = i;
                    break;
                }
            }

            if (loginButtonIndex != -1) {
                JPanel loginPanel = new JPanel(new GridLayout(1, 2, 5, 0));
                loginPanel.setBackground(LIGHT_CLOUD);
                loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                loginPanel.setMaximumSize(new Dimension(300, 40));

                loginButton.setMaximumSize(null);
                loginButton.setText("Compte");
                loginPanel.add(loginButton);

                disconnectButton.setMaximumSize(null);
                loginPanel.add(disconnectButton);

                mainPanel.remove(loginButtonIndex);
                mainPanel.add(loginPanel, loginButtonIndex);
            }
        } else {
            loginButton.setText("Connexion");
            loginButton.setMaximumSize(new Dimension(300, 40));
            Component[] components = mainPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof JPanel panel && i > 0) {
                    if (panel.getComponentCount() == 3) {
                        boolean hasLoginButton = false;
                        for (Component comp : panel.getComponents()) {
                            if (comp == loginButton) {
                                hasLoginButton = true;
                                break;
                            }
                        }
                        if (hasLoginButton) {
                            mainPanel.remove(i);
                            mainPanel.add(loginButton, i);
                            break;
                        }
                    }
                }
            }
        }
        editorButton.setVisible(currentUser != null && currentUser instanceof Admin);

        revalidate();
        repaint();
    }

    private void launchMainGame() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord vous connecter !", "Connexion requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!(currentUser instanceof Player player)) {
            JOptionPane.showMessageDialog(this, "Seuls les joueurs peuvent jouer !", "Accès refusé", JOptionPane.WARNING_MESSAGE);
            return;
        }

        showFrozenOverlay();

        // Create game controller with dependency injection
        GameController gameController = serviceFactory.createGameController(player);

        // Create as a modal dialog instead of a frame
        JDialog gameDialog = new JDialog(this, "WordCrafter - Mode de jeu principal", true);
        MainGameFrame gameFrame = new MainGameFrame(currentUser, gameController, gameController.getGameStateManager());
        gameLauncher(gameDialog, gameFrame.getContentPane(), gameFrame.getSize());
    }
    
    private void launchFreeBuild() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord vous connecter !", "Connexion requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!(currentUser instanceof Player player)) {
            JOptionPane.showMessageDialog(this, "Seuls les joueurs peuvent jouer !", "Accès refusé", JOptionPane.WARNING_MESSAGE);
            return;
        }

        showFrozenOverlay();

        // Create game controller with dependency injection
        GameController gameController = serviceFactory.createGameController(player);

        JDialog freeBuildDialog = new JDialog(this, "WordCrafter - Mode construction libre", true);
        FreeBuildFrame freeBuildFrame = new FreeBuildFrame(currentUser, gameController, gameController.getGameStateManager());
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
        if (currentUser != null) {
            AccountSettingsFrame accountSettings = new AccountSettingsFrame(currentUser, serviceFactory.getAuthService(), this::logout);
            accountSettings.setVisible(true);
        } else {
            LoginDialog loginDialog = new LoginDialog(this, serviceFactory.createAuthController());
            loginDialog.setVisible(true);

            if (loginDialog.getAuthenticatedUser() != null) {
                setCurrentUser(loginDialog.getAuthenticatedUser());
            }
        }
    }

    private void logout() {
        currentUser = null;
        updateUI();
    }
}


