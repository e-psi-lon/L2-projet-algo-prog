package io.github.e_psi_lon.wordcrafter.ui.game;

import io.github.e_psi_lon.wordcrafter.controller.GameController;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.service.GameStateListener;
import io.github.e_psi_lon.wordcrafter.service.GameStateManager;
import io.github.e_psi_lon.wordcrafter.ui.AppColors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Free build mode - construct words from any morphemes with points based on complexity.
 * Points = number of morphemes * 10 (e.g., 3 morphemes = 30 points)
 */
public class FreeBuildFrame extends GameFrame implements GameStateListener {
    private JList<Morpheme> morphemeList;
    private DefaultListModel<String> constructedWordsModel;

    public FreeBuildFrame(GameController gameController, @NotNull GameStateManager gameStateManager) {
        super(gameController, gameStateManager, "WordCrafter - Mode construction libre");

        // Register as a listener for game state changes
        gameStateManager.addListener(this);

        setSize(900, 600);
        loadMorphemes();
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel - Instructions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LIGHT_CLOUD);

        JLabel instructionLabel = new JLabel("<html><center><b>Mode Construction Libre</b><br>" +
                "Sélectionnez des morphèmes pour construire un mot. Points = nombre de morphèmes + (nombres de morphèmes - 1)²</center></html>");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setForeground(AppColors.TITLE_TEXT);
        topPanel.add(instructionLabel, BorderLayout.NORTH);

        // Word construction panel
        JPanel wordPanel = new JPanel(new BorderLayout());
        wordPanel.setBackground(LIGHT_CLOUD);
        wordPanel.setBorder(BorderFactory.createTitledBorder("Mot en construction"));

        constructedWordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        constructedWordPanel.setBackground(Color.WHITE);
        constructedWordPanel.setPreferredSize(new Dimension(700, 60));
        wordPanel.add(constructedWordPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        wordPanel.add(buttonPanel, BorderLayout.SOUTH);

        topPanel.add(wordPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Left panel - Available morphemes list
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(LIGHT_CLOUD);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Morphèmes disponibles"));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        DefaultListModel<Morpheme> morphemeListModel = new DefaultListModel<>();
        for (Morpheme morpheme : availableMorphemes) {
            morphemeListModel.addElement(morpheme);
        }

        morphemeList = new JList<>(morphemeListModel);
        morphemeList.setBackground(Color.WHITE);
        morphemeList.setCellRenderer(new MorphemeCellRenderer());
        morphemeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Morpheme selected = morphemeList.getSelectedValue();
                if (selected != null) {
                    gameController.handleMorphemeToggle(selected);
                    morphemeList.clearSelection();
                }
            }
        });

        JScrollPane morphemeScrollPane = new JScrollPane(morphemeList);
        leftPanel.add(morphemeScrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right panel - Score and constructed words
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(LIGHT_CLOUD);
        rightPanel.setPreferredSize(new Dimension(250, 0));

        scoreLabel = new JLabel("Score: " + gameStateManager.getCurrentScore());
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(scoreLabel, BorderLayout.NORTH);

        JPanel wordsPanel = new JPanel(new BorderLayout());
        wordsPanel.setBorder(BorderFactory.createTitledBorder("Mots créés"));
        wordsPanel.setBackground(LIGHT_CLOUD);

        constructedWordsModel = new DefaultListModel<>();
        JList<String> constructedWordsList = new JList<>(constructedWordsModel);
        constructedWordsList.setBackground(Color.WHITE);
        JScrollPane wordsScrollPane = new JScrollPane(constructedWordsList);
        wordsPanel.add(wordsScrollPane, BorderLayout.CENTER);

        rightPanel.add(wordsPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private @NotNull JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(LIGHT_CLOUD);

        JButton submitButton = new JButton("Créer le mot");
        submitButton.setBackground(BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> submitWord());
        buttonPanel.add(submitButton);

        JButton clearButton = new JButton("Effacer");
        clearButton.setBackground(BUTTON_COLOR);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> gameController.handleClearSelection());
        buttonPanel.add(clearButton);

        return buttonPanel;
    }

    protected void updateConstructedWordDisplay() {
        constructedWordPanel.removeAll();
        List<Morpheme> selected = gameStateManager.getSelectedMorphemes();

        if (selected.isEmpty()) {
            JLabel emptyLabel = new JLabel("(Cliquez sur des morphèmes pour construire un mot)");
            emptyLabel.setForeground(Color.GRAY);
            constructedWordPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < selected.size(); i++) {
                Morpheme morpheme = selected.get(i);
                JButton morphemeButton = new JButton(morpheme.text());
                morphemeButton.setBackground(SELECTED_COLOR);
                morphemeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
                morphemeButton.setToolTipText("Cliquez pour retirer: " + morpheme.definition());
                morphemeButton.addActionListener(e -> gameController.handleMorphemeToggle(morpheme));
                constructedWordPanel.add(morphemeButton);

                if (i < selected.size() - 1) {
                    JLabel plusLabel = new JLabel(" + ");
                    plusLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
                    constructedWordPanel.add(plusLabel);
                }
            }

            // Show potential points using scaling formula: n + (n-1)²
            int n = selected.size();
            int potentialPoints = n + (n - 1) * (n - 1);
            JLabel pointsLabel = new JLabel(" = " + potentialPoints + " pts");
            pointsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            pointsLabel.setForeground(AppColors.SUCCESS_TEXT);
            constructedWordPanel.add(pointsLabel);
        }

        constructedWordPanel.revalidate();
        constructedWordPanel.repaint();
    }

    private void submitWord() {
        List<Morpheme> selected = gameStateManager.getSelectedMorphemes();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord sélectionner des morphèmes !",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build the word text
        StringBuilder wordText = new StringBuilder();
        for (Morpheme m : selected) {
            wordText.append(m.text());
        }

        // Ask for a definition
        String definition = JOptionPane.showInputDialog(this,
                "Veuillez fournir une définition pour le mot '" + wordText + "' :",
                "Définition requise",
                JOptionPane.QUESTION_MESSAGE);

        if (definition == null || definition.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Une définition est requise pour valider le mot.",
                    "Définition manquante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate definition matches the morphemes (heuristic check)
        String validationError = validateDefinition(definition, selected);
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError,
                    "Définition invalide", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate points using scaling formula: n + (n-1)²
        // This rewards longer words: 1→1pt, 2→3pts, 3→7pts, 4→12pts, 5→19pts
        int n = selected.size();
        int points = n + (n - 1) * (n - 1);

        // Award points directly (free build doesn't validate against database)
        gameController.awardPoints(points);

        constructedWordsModel.addElement(wordText + " (" + points + " pts)");

        // Success message
        JOptionPane.showMessageDialog(this,
                "Mot créé avec succès !\n\n" +
                "Mot: " + wordText + "\n" +
                "Définition: " + definition + "\n" +
                "Morphèmes: " + selected.size() + "\n" +
                "Points gagnés: " + points,
                "Succès", JOptionPane.INFORMATION_MESSAGE);

        gameController.handleClearSelection();
    }

    /**
     * Validates that the user's definition contains at least one word from each morpheme's definition.
     * This is a heuristic check to ensure the definition is somewhat related to the morphemes.
     *
     * @param userDefinition The definition provided by the user
     * @param morphemes The morphemes used in the word
     * @return An error message if validation fails, null if valid
     */
    private @Nullable String validateDefinition(@NotNull String userDefinition, @NotNull List<Morpheme> morphemes) {
        String normalizedUserDef = userDefinition.toLowerCase().trim();

        // Split into words and remove punctuation
        String[] userWords = normalizedUserDef.split("[\\s.,;:!?]+");

        for (Morpheme morpheme : morphemes) {
            String morphemeDef = morpheme.definition().toLowerCase();
            String[] morphemeWords = morphemeDef.split("[\\s.,;:!?]+");

            boolean foundMatch = false;

            // Check if at least one word from the morpheme definition appears in the user definition
            for (String morphemeWord : morphemeWords) {
                if (morphemeWord.length() < 3) continue; // Skip short words like "de", "un", etc.

                for (String userWord : userWords) {
                    if (userWord.equals(morphemeWord) || userWord.contains(morphemeWord) || morphemeWord.contains(userWord)) {
                        foundMatch = true;
                        break;
                    }
                }

                if (foundMatch) break;
            }

            if (!foundMatch) {
                return "La définition doit inclure au moins un mot relatif au morphème '" +
                       morpheme.text() + "' (" + morpheme.definition() + ").";
            }
        }

        return null; // Valid
    }

    @Override
    public void onGameStateChanged(GameStateManager.@NotNull GameStateEvent event) {
        switch (event.type()) {
            case MORPHEME_SELECTED:
            case MORPHEME_DESELECTED:
            case SELECTION_CLEARED:
            case ROUND_RESET:
                updateConstructedWordDisplay();
                break;
            case SCORE_UPDATED:
                scoreLabel.setText("Score: " + gameStateManager.getCurrentScore());
                break;
        }
    }

    /**
     * Custom cell renderer for morpheme list
     */
    private static class MorphemeCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Morpheme morpheme) {
                label.setText("<html><b>" + morpheme.text() + "</b><br><small>" + morpheme.definition() + "</small></html>");
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }

            if (!isSelected) {
                label.setBackground(AppColors.MORPHEME_COLOR);
            }

            return label;
        }
    }
}

