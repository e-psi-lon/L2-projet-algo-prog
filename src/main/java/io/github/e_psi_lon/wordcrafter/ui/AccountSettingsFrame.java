package io.github.e_psi_lon.wordcrafter.ui;

import io.github.e_psi_lon.wordcrafter.service.AuthService;
import io.github.e_psi_lon.wordcrafter.model.User;
import io.github.e_psi_lon.wordcrafter.model.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Account settings frame for logged-in users.
 * Allows changing password, username, and deleting account.
 */
public class AccountSettingsFrame extends JFrame {
    private static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    private static final Color BUTTON_COLOR = new Color(255, 182, 193);
    private User currentUser;
    private final Runnable onAccountDeleted;
    private final AuthService authService;

    public AccountSettingsFrame(User currentUser, AuthService authService, Runnable onAccountDeleted) {
        this.currentUser = currentUser;
        this.authService = authService;
        this.onAccountDeleted = onAccountDeleted;

        setTitle("WordCrafter - Paramètres du compte");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
    }

    private void initComponents() {
        // Create a scrollable panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(LIGHT_CLOUD);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.gridx = 0;

        // Title
        JLabel titleLabel = new JLabel("Paramètres du compte");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        // Username display
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Nom d'utilisateur:"), gbc);
        gbc.gridx = 1;
        JLabel usernameLabel = new JLabel(currentUser.getUsername());
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(usernameLabel, gbc);

        // Score display (if player)
        if (currentUser instanceof Player player) {
            gbc.gridx = 0;
            gbc.gridy = 2;
            contentPanel.add(new JLabel("Score:"), gbc);
            gbc.gridx = 1;
            JLabel scoreLabel = new JLabel(String.valueOf(player.getScore()));
            scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            contentPanel.add(scoreLabel, gbc);
        }

        // Change username section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JSeparator separatorUsername = new JSeparator();
        contentPanel.add(separatorUsername, gbc);

        gbc.gridy = 4;
        JLabel changeUsernameLabel = new JLabel("Changer le nom d'utilisateur:");
        changeUsernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPanel.add(changeUsernameLabel, gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Nouveau nom d'utilisateur:"), gbc);
        gbc.gridx = 1;
        JTextField newUsernameField = new JTextField(30);
        newUsernameField.setPreferredSize(new Dimension(250, 30));
        contentPanel.add(newUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton changeUsernameButton = new JButton("Changer le nom d'utilisateur");
        changeUsernameButton.setBackground(BUTTON_COLOR);
        changeUsernameButton.setForeground(Color.WHITE);
        changeUsernameButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        changeUsernameButton.setPreferredSize(new Dimension(250, 40));
        changeUsernameButton.addActionListener(e -> changeUsername(newUsernameField, usernameLabel));
        contentPanel.add(changeUsernameButton, gbc);

        // Change password section
        gbc.gridy = 7;
        JSeparator separator1 = new JSeparator();
        contentPanel.add(separator1, gbc);

        gbc.gridy = 8;
        JLabel changePasswordLabel = new JLabel("Changer le mot de passe:");
        changePasswordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPanel.add(changePasswordLabel, gbc);

        gbc.gridy = 9;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Ancien mot de passe:"), gbc);
        gbc.gridx = 1;
        JPasswordField oldPasswordField = new JPasswordField(30);
        oldPasswordField.setPreferredSize(new Dimension(250, 30));
        contentPanel.add(oldPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        contentPanel.add(new JLabel("Nouveau mot de passe:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPasswordField = new JPasswordField(30);
        newPasswordField.setPreferredSize(new Dimension(250, 30));
        contentPanel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        contentPanel.add(new JLabel("Confirmer le mot de passe:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(30);
        confirmPasswordField.setPreferredSize(new Dimension(250, 30));
        contentPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        JButton changePasswordButton = new JButton("Changer le mot de passe");
        changePasswordButton.setBackground(BUTTON_COLOR);
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        changePasswordButton.setPreferredSize(new Dimension(250, 40));
        changePasswordButton.addActionListener(e -> changePassword(oldPasswordField, newPasswordField, confirmPasswordField));
        contentPanel.add(changePasswordButton, gbc);

        // Dangerous operations section
        gbc.gridy = 13;
        JSeparator separator2 = new JSeparator();
        contentPanel.add(separator2, gbc);

        gbc.gridy = 14;
        JLabel dangerLabel = new JLabel("Zone dangereuse:");
        dangerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dangerLabel.setForeground(new Color(220, 20, 60)); // Crimson
        contentPanel.add(dangerLabel, gbc);

        gbc.gridy = 15;
        JButton deleteAccountButton = new JButton("Supprimer le compte");
        deleteAccountButton.setBackground(new Color(220, 20, 60)); // Crimson
        deleteAccountButton.setForeground(Color.WHITE);
        deleteAccountButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        deleteAccountButton.setPreferredSize(new Dimension(250, 40));
        deleteAccountButton.addActionListener(e -> deleteAccount());
        contentPanel.add(deleteAccountButton, gbc);

        // Close button
        gbc.gridy = 16;
        JButton closeButton = new JButton("Fermer");
        closeButton.setBackground(new Color(200, 200, 200));
        closeButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        closeButton.setPreferredSize(new Dimension(250, 40));
        closeButton.addActionListener(e -> dispose());
        contentPanel.add(closeButton, gbc);

        gbc.gridy = 17;
        gbc.weighty = 1.0;
        contentPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(LIGHT_CLOUD);
        add(scrollPane);
    }

    private void changeUsername(JTextField newUsernameField, JLabel usernameLabel) {
        String newUsername = newUsernameField.getText().trim();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nouveau nom d'utilisateur!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newUsername.length() < 3) {
            JOptionPane.showMessageDialog(this, "Le nom d'utilisateur doit contenir au moins 3 caractères!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        authService.updateUsername(currentUser.getId(), newUsername);
        usernameLabel.setText(newUsername);
        currentUser = new Player(currentUser.getId(), newUsername, "", 0);

        JOptionPane.showMessageDialog(this, "Nom d'utilisateur changé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        newUsernameField.setText("");
    }

    private void changePassword(JPasswordField oldPasswordField, JPasswordField newPasswordField, JPasswordField confirmPasswordField) {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Les nouveaux mots de passe ne correspondent pas!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 4) {
            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 4 caractères!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verify old password by attempting authentication
        User authenticatedUser = authService.authenticate(currentUser.getUsername(), oldPassword);

        if (authenticatedUser == null) {
            JOptionPane.showMessageDialog(this, "Ancien mot de passe incorrect!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update password
        authService.updatePassword(currentUser.getId(), newPassword);

        JOptionPane.showMessageDialog(this, "Mot de passe changé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr? Cette action est irréversible et supprimera tous vos données.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.deleteAccount(currentUser.getId());
            JOptionPane.showMessageDialog(this, "Compte supprimé.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            onAccountDeleted.run();
            dispose();
        }
    }
}

