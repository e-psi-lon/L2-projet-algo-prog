package io.github.e_psi_lon.wordcrafter.ui;

import io.github.e_psi_lon.wordcrafter.controller.EditorController;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.Word;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Editor frame for administrators to add morphemes and words to the database.
 */
public class EditorFrame extends JFrame {
    private static final Color LIGHT_CLOUD = AppColors.LIGHT_CLOUD;
    private static final Color BUTTON_COLOR = AppColors.BUTTON_COLOR;
    private final EditorController editorController;
    private List<Morpheme> allMorphemes;
    private List<Word> allWords;
    private DefaultListModel<String> morphemeListModel;
    private DefaultListModel<String> wordListModel;

    public EditorFrame(@NotNull EditorController editorController) {
        this.editorController = editorController;
        setTitle("WordCrafter - Mode Éditeur");
        setSize(1200, 700);
        setLocationRelativeTo(null);

        allMorphemes = editorController.getAllMorphemes();
        allWords = editorController.getAllWords();

        initComponents();
    }

    private void reloadMorphemes() {
        allMorphemes = editorController.getAllMorphemes();
        updateMorphemeList("", morphemeListModel);
    }

    private void reloadWords() {
        allWords = editorController.getAllWords();
        updateWordList("", wordListModel);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        morphemeListModel = new DefaultListModel<>();
        wordListModel = new DefaultListModel<>();

        // Left side: Tabs for adding morphemes and words
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LIGHT_CLOUD);

        // Morpheme tab
        JPanel morphemePanel = createMorphemePanel();
        tabbedPane.addTab("Ajouter un morphème", morphemePanel);

        // Word tab
        JPanel wordPanel = createWordPanel();
        tabbedPane.addTab("Ajouter un mot", wordPanel);

        // Admin creation tab (secure - only accessible from editor)
        JPanel adminPanel = createAdminPanel();
        tabbedPane.addTab("Créer Admin", adminPanel);

        // Right side: Searchable lists for morphemes and words
        JPanel listsPanel = createListsPanel();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(listsPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private @NotNull JPanel createMorphemePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_CLOUD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Morpheme text
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Texte du morphème:"), gbc);

        gbc.gridx = 1;
        JTextField morphemeTextField = new JTextField(20);
        panel.add(morphemeTextField, gbc);

        // Morpheme definition
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Définition:"), gbc);

        gbc.gridx = 1;
        JTextField definitionTextField = new JTextField(20);
        panel.add(definitionTextField, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = createAddMorphemeButton(morphemeTextField, definitionTextField);
        panel.add(addButton, gbc);

        return panel;
    }

    private @NotNull JButton createAddMorphemeButton(JTextField morphemeTextField, JTextField definitionTextField) {
        JButton addButton = new JButton("Ajouter un morphème");
        addButton.setBackground(BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            String text = morphemeTextField.getText().trim();
            String definition = definitionTextField.getText().trim();
            if (!text.isEmpty() && !definition.isEmpty()) {
                editorController.handleAddMorpheme(text, definition);
                JOptionPane.showMessageDialog(this, "Morphème ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                morphemeTextField.setText("");
                definitionTextField.setText("");
                // Reload morphemes from database and update list
                reloadMorphemes();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        return addButton;
    }

    private @NotNull JPanel createWordPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LIGHT_CLOUD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Word text
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mot:"), gbc);

        gbc.gridx = 1;
        JTextField wordTextField = new JTextField(20);
        formPanel.add(wordTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("IDs des morphèmes (ex: 1,3,5):"), gbc);

        gbc.gridx = 1;
        JTextField morphemeIdsField = new JTextField(20);
        formPanel.add(morphemeIdsField, gbc);

        // Definition
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Définition:"), gbc);

        gbc.gridx = 1;
        JTextField definitionTextField = new JTextField(20);
        formPanel.add(definitionTextField, gbc);

        // Points
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Points:"), gbc);

        gbc.gridx = 1;
        JSpinner pointsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        formPanel.add(pointsSpinner, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Ajouter un mot");
        addButton.setBackground(BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            String word = wordTextField.getText().trim();
            String idsText = morphemeIdsField.getText().trim();
            String definition = definitionTextField.getText().trim();

            if (!word.isEmpty() && !idsText.isEmpty() && !definition.isEmpty()) {
                try {
                    // Parse comma-separated IDs
                    List<Integer> ids = new ArrayList<>();
                    for (String idStr : idsText.split(",")) {
                        ids.add(Integer.parseInt(idStr.trim()));
                    }

                    int points = (Integer) pointsSpinner.getValue();

                    editorController.handleAddWord(word, ids, points, definition);
                    JOptionPane.showMessageDialog(this, "Mot ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    wordTextField.setText("");
                    morphemeIdsField.setText("");
                    definitionTextField.setText("");
                    pointsSpinner.setValue(5);
                    // Reload words from database and update list
                    reloadWords();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: IDs invalides. Utilisez le format: 1, 3, 5", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(addButton, gbc);

        return formPanel;
    }

    private @NotNull JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_CLOUD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nom d'utilisateur:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // Confirm password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirmer mot de passe:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton createButton = new JButton("Créer administrateur");
        createButton.setBackground(BUTTON_COLOR);
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = editorController.handleCreateAdmin(username, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Administrateur créé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                usernameField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Ce nom d'utilisateur existe déjà!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(createButton, gbc);

        return panel;
    }

    private @NotNull JPanel createListsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(LIGHT_CLOUD);
        panel.setBorder(BorderFactory.createTitledBorder("Base de données"));
        panel.setPreferredSize(new Dimension(280, 600));

        JTabbedPane listsTabbed = new JTabbedPane();
        listsTabbed.setBackground(LIGHT_CLOUD);

        // Morpheme list tab
        JPanel morphemeListPanel = createMorphemeListPanel();
        listsTabbed.addTab("Morphèmes", morphemeListPanel);

        // Word list tab
        JPanel wordListPanel = createWordListPanel();
        listsTabbed.addTab("Mots", wordListPanel);

        panel.add(listsTabbed, BorderLayout.CENTER);
        return panel;
    }

    private @NotNull JPanel createMorphemeListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(LIGHT_CLOUD);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(LIGHT_CLOUD);
        searchPanel.add(new JLabel("Rechercher:"), BorderLayout.WEST);

        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // List model and list
        JList<String> morphemeList = createMorphemeList(morphemeListModel);

        // Populate list initially
        updateMorphemeList("", morphemeListModel);

        // Search functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearch();
            }

            private void updateSearch() {
                updateMorphemeList(searchField.getText(), morphemeListModel);
            }
        });

        JScrollPane scrollPane = new JScrollPane(morphemeList);
        scrollPane.setPreferredSize(new Dimension(250, 500));

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private @NotNull JPanel createWordListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(LIGHT_CLOUD);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(LIGHT_CLOUD);
        searchPanel.add(new JLabel("Rechercher:"), BorderLayout.WEST);

        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // List model and list
        JList<String> wordList = createWordList(wordListModel);

        // Populate list initially
        updateWordList("", wordListModel);

        // Search functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearch();
            }

            private void updateSearch() {
                updateWordList(searchField.getText(), wordListModel);
            }
        });

        JScrollPane scrollPane = new JScrollPane(wordList);
        scrollPane.setPreferredSize(new Dimension(250, 500));

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private @NotNull JList<String> createMorphemeList(DefaultListModel<String> listModel) {
        JList<String> morphemeList = new JList<>(listModel);
        morphemeList.setBackground(Color.WHITE);
        morphemeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(getFont().deriveFont(10f));
                return comp;
            }
        });
        return morphemeList;
    }

    private @NotNull JList<String> createWordList(DefaultListModel<String> listModel) {
        JList<String> wordList = new JList<>(listModel);
        wordList.setBackground(Color.WHITE);
        wordList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(getFont().deriveFont(10f));
                return comp;
            }
        });
        return wordList;
    }

    private void updateMorphemeList(@NotNull String filter, @NotNull DefaultListModel<String> model) {
        model.clear();
        String filterLower = filter.toLowerCase();

        for (Morpheme morpheme : allMorphemes) {
            if (morpheme.text().toLowerCase().contains(filterLower) ||
                    String.valueOf(morpheme.id()).contains(filterLower)) {
                model.addElement(morpheme.id() + " - " + morpheme.text());
            }
        }
    }

    private void updateWordList(@NotNull String filter, @NotNull DefaultListModel<String> model) {
        model.clear();
        String filterLower = filter.toLowerCase();

        for (Word word : allWords) {
            if (word.text().toLowerCase().contains(filterLower) ||
                    String.valueOf(word.id()).contains(filterLower)) {
                model.addElement(word.id() + " - " + word.text());
            }
        }
    }
}

