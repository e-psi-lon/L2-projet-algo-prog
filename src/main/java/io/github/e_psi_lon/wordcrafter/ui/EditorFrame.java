package io.github.e_psi_lon.wordcrafter.ui;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Editor frame for administrators to add morphemes and words to the database.
 */
public class EditorFrame extends JFrame {
    private static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    private static final Color BUTTON_COLOR = new Color(255, 182, 193);

    public EditorFrame() {
        setTitle("WordCrafter - Editor Mode");
        setSize(500, 400);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LIGHT_CLOUD);

        // Morpheme tab
        JPanel morphemePanel = createMorphemePanel();
        tabbedPane.addTab("Add Morpheme", morphemePanel);

        // Word tab
        JPanel wordPanel = createWordPanel();
        tabbedPane.addTab("Add Word", wordPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createMorphemePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_CLOUD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Morpheme text
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Morpheme Text:"), gbc);

        gbc.gridx = 1;
        JTextField morphemeTextField = new JTextField(20);
        panel.add(morphemeTextField, gbc);

        // Morpheme type
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        JComboBox<Morpheme.MorphemeType> typeCombo = new JComboBox<>(Morpheme.MorphemeType.values());
        panel.add(typeCombo, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Morpheme");
        addButton.setBackground(BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            String text = morphemeTextField.getText().trim();
            if (!text.isEmpty()) {
                DatabaseManager.getInstance().addMorpheme(text, (Morpheme.MorphemeType) typeCombo.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Morpheme added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                morphemeTextField.setText("");
            }
        });
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel createWordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_CLOUD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Word text
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Word:"), gbc);

        gbc.gridx = 1;
        JTextField wordTextField = new JTextField(20);
        panel.add(wordTextField, gbc);

        // Morpheme IDs
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Morpheme IDs (comma-separated):"), gbc);

        gbc.gridx = 1;
        JTextField morphemeIdsField = new JTextField(20);
        panel.add(morphemeIdsField, gbc);

        // Points
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Points:"), gbc);

        gbc.gridx = 1;
        JSpinner pointsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        panel.add(pointsSpinner, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Word");
        addButton.setBackground(BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            String word = wordTextField.getText().trim();
            String idsStr = morphemeIdsField.getText().trim();

            if (!word.isEmpty() && !idsStr.isEmpty()) {
                try {
                    java.util.List<Integer> ids = Arrays.stream(idsStr.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .toList();
                    int points = (Integer) pointsSpinner.getValue();

                    DatabaseManager.getInstance().addWord(word, ids, points);
                    JOptionPane.showMessageDialog(this, "Word added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    wordTextField.setText("");
                    morphemeIdsField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid morpheme IDs format!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(addButton, gbc);

        return panel;
    }
}

