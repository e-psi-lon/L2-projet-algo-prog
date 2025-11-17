package io.github.e_psi_lon.wordcrafter.ui;

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
        JLabel userLabel = new JLabel("Not logged in");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Game mode buttons
        JButton mainGameButton = createStyledButton("Main Game Mode");
        mainGameButton.addActionListener(e -> launchMainGame());
        mainPanel.add(mainGameButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton freeBuildButton = createStyledButton("Free Build Mode");
        freeBuildButton.addActionListener(e -> launchFreeBuild());
        mainPanel.add(freeBuildButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton prefixMatcherButton = createStyledButton("Prefix Matcher Mode");
        prefixMatcherButton.addActionListener(e -> launchPrefixMatcher());
        mainPanel.add(prefixMatcherButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Editor button (only visible for admins)
        JButton editorButton = createStyledButton("Editor Mode");
        editorButton.addActionListener(e -> launchEditor());
        editorButton.setVisible(false);
        mainPanel.add(editorButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Other buttons
        JButton settingsButton = createStyledButton("Settings");
        settingsButton.addActionListener(e -> showSettings());
        mainPanel.add(settingsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton loginButton = createStyledButton("Login / Account");
        loginButton.addActionListener(e -> showLogin());
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton quitButton = createStyledButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(quitButton);

        add(mainPanel);
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
        // Update user label and show/hide editor button based on user role
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel) comp).getText().contains("connect")) {
                if (currentUser != null) {
                    ((JLabel) comp).setText("Bienvenue, " + currentUser.getUsername() + " (Score: " + currentUser.getScore() + ")");
                } else {
                    ((JLabel) comp).setText("Non connecté");
                }
            }
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Mode éditeur")) {
                comp.setVisible(currentUser != null && currentUser.isAdmin());
            }
        }
        revalidate();
        repaint();
    }
    
    private void launchMainGame() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord vous connecter !", "Connexion requise", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MainGameFrame gameFrame = new MainGameFrame(currentUser);
        gameFrame.setVisible(true);
    }
    
    private void launchFreeBuild() {
        FreeBuildFrame freeBuildFrame = new FreeBuildFrame();
        freeBuildFrame.setVisible(true);
    }
    
    private void launchPrefixMatcher() {
        PrefixMatcherFrame prefixMatcherFrame = new PrefixMatcherFrame();
        prefixMatcherFrame.setVisible(true);
    }
    
    private void launchEditor() {
        if (currentUser == null || !currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Accès administrateur requis !", "Accès refusé", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EditorFrame editorFrame = new EditorFrame();
        editorFrame.setVisible(true);
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


