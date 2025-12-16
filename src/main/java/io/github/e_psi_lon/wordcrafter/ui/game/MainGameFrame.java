package io.github.e_psi_lon.wordcrafter.ui.game;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.Player;
import io.github.e_psi_lon.wordcrafter.model.User;
import io.github.e_psi_lon.wordcrafter.model.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game mode - morpheme grid that can be combined to make words
 */
public class MainGameFrame extends GameFrame {
    private final User currentUser;
    private List<Morpheme> availableMorphemes;
    private final List<Morpheme> selectedMorphemes;
    private final List<Word> constructedWords;

    private JPanel gridPanel;
    private JPanel constructedWordPanel;
    private JLabel scoreLabel;
    private JList<String> constructedWordsList;
    private DefaultListModel<String> constructedWordsModel;

    public MainGameFrame(User user) {
        this.currentUser = user;
        this.selectedMorphemes = new ArrayList<>();
        this.constructedWords = new ArrayList<>();

        setTitle("WordCrafter - Mode de jeu principal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadMorphemes();
        initComponents();
    }

    private void loadMorphemes() {
        availableMorphemes = DatabaseManager.getInstance().getAllMorphemes();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_CLOUD);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel - Word-building area
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LIGHT_CLOUD);
        topPanel.setBorder(BorderFactory.createTitledBorder("Mot construit"));

        constructedWordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        constructedWordPanel.setBackground(Color.WHITE);
        constructedWordPanel.setPreferredSize(new Dimension(600, 60));
        topPanel.add(constructedWordPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();

        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Central panel - morphemes grid
        gridPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        gridPanel.setBackground(LIGHT_CLOUD);

        for (int i = 0; i < Math.min(9, availableMorphemes.size()); i++) {
            Morpheme morpheme = availableMorphemes.get(i);
            JButton morphemeButton = createMorphemeButton(morpheme);
            gridPanel.add(morphemeButton);
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Right panel - info and constructed words list
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(LIGHT_CLOUD);
        rightPanel.setPreferredSize(new Dimension(200, 0));

        scoreLabel = new JLabel("Score: " + (currentUser instanceof Player ? ((Player) currentUser).getScore() : 0));
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(scoreLabel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Mots trouvés"));
        listPanel.setBackground(LIGHT_CLOUD);

        constructedWordsModel = new DefaultListModel<>();
        constructedWordsList = new JList<>(constructedWordsModel);
        constructedWordsList.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(constructedWordsList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        rightPanel.add(listPanel, BorderLayout.CENTER);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(LIGHT_CLOUD);

        JButton checkButton = new JButton("Vérifier le mot");
        checkButton.setBackground(BUTTON_COLOR);
        checkButton.setForeground(Color.WHITE);
        checkButton.addActionListener(e -> checkWord());
        buttonPanel.add(checkButton);

        JButton clearButton = new JButton("Effacer");
        clearButton.setBackground(BUTTON_COLOR);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearSelection());
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

    private JButton createMorphemeButton(Morpheme morpheme) {
        JButton button = new JButton("<html><center>" + morpheme.text() + "<br><small>" +
                                     morpheme.definition() + "</small></center></html>");
        button.setBackground(MORPHEME_COLOR);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PASTEL_PINK, 2));

        button.addActionListener(e -> {
            if (selectedMorphemes.contains(morpheme)) {
                selectedMorphemes.remove(morpheme);
                button.setBackground(MORPHEME_COLOR);
            } else {
                selectedMorphemes.add(morpheme);
                button.setBackground(SELECTED_COLOR);
            }
            updateConstructedWordDisplay();
        });

        return button;
    }

    private void updateConstructedWordDisplay() {
        constructedWordPanel.removeAll();

        for (int i = 0; i < selectedMorphemes.size(); i++) {
            Morpheme morpheme = selectedMorphemes.get(i);
            JLabel label = new JLabel(morpheme.text());
            label.setFont(new Font("SansSerif", Font.BOLD, 18));
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Make the label clickable to delete the morpheme
            final int index = i;
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedMorphemes.remove(index);
                    updateConstructedWordDisplay();
                    updateGridButtons();
                }
            });
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            constructedWordPanel.add(label);

            if (i < selectedMorphemes.size() - 1) {
                constructedWordPanel.add(new JLabel(" + "));
            }
        }

        constructedWordPanel.revalidate();
        constructedWordPanel.repaint();
    }

    private void updateGridButtons() {
        Component[] components = gridPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton button) {
                String buttonText = button.getText().replaceAll("<[^>]*>", "").trim();

                boolean isSelected = selectedMorphemes.stream()
                    .anyMatch(m -> buttonText.contains(m.text()));

                button.setBackground(isSelected ? SELECTED_COLOR : MORPHEME_COLOR);
            }
        }
    }

    private void checkWord() {
        if (selectedMorphemes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord sélectionner des morphèmes !",
                                        "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build the text of the word
        StringBuilder wordText = new StringBuilder();
        for (Morpheme m : selectedMorphemes) {
            wordText.append(m.text());
        }

        // Get IDs of the morphemes
        List<Integer> morphemeIds = selectedMorphemes.stream()
            .map(Morpheme::id)
            .toList();

        // Validate against the database
        Word validWord = DatabaseManager.getInstance().validateWord(wordText.toString(), morphemeIds);

        if (validWord != null) {
            // The word is valid
            constructedWords.add(validWord);
            constructedWordsModel.addElement(validWord.text() + " (" + validWord.points() + " pts)");

            // Update the score
            int currentScore = currentUser instanceof Player ? ((Player) currentUser).getScore() : 0;
            int newScore = currentScore + validWord.points();
            DatabaseManager.getInstance().updateUserScore(currentUser.getId(), validWord.points());
            DatabaseManager.getInstance().addPlayerWord(currentUser.getId(), validWord.id());
            scoreLabel.setText("Score: " + newScore);

            JOptionPane.showMessageDialog(this,
                "Mot valide ! Vous avez gagné " + validWord.points() + " points !",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            clearSelection();
        } else {
            JOptionPane.showMessageDialog(this,
                "Mot invalide : " + wordText,
                "Invalide", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSelection() {
        selectedMorphemes.clear();
        updateConstructedWordDisplay();
        updateGridButtons();
    }
}

