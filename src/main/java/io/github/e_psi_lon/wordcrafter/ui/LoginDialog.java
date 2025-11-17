package io.github.e_psi_lon.wordcrafter.ui;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Login/registration dialog.
 */
public class LoginDialog extends JDialog {
    private User authenticatedUser;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    private static final Color BUTTON_COLOR = new Color(255, 182, 193);

    public LoginDialog(Frame parent) {
        super(parent, "Login / Register", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(LIGHT_CLOUD);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> login());
        buttonPanel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(BUTTON_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(BUTTON_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        authenticatedUser = DatabaseManager.getInstance().authenticateUser(username, password);
        
        if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, "Connexion réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Nom d'utilisateur ou mot de passe invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = DatabaseManager.getInstance().createUser(username, password, User.UserRole.PLAYER);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Inscription réussie ! Vous pouvez maintenant vous connecter.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Ce nom d'utilisateur existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}

