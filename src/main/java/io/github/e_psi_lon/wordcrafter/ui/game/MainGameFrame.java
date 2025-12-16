package io.github.e_psi_lon.wordcrafter.ui.game;

import io.github.e_psi_lon.wordcrafter.controller.GameController;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.User;
import io.github.e_psi_lon.wordcrafter.model.Word;
import io.github.e_psi_lon.wordcrafter.service.GameStateListener;
import io.github.e_psi_lon.wordcrafter.service.GameStateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main game mode - morpheme grid that can be combined to make words
 */
public class MainGameFrame extends GameFrame implements GameStateListener {
    private final User currentUser;
    private final GameController gameController;
    private final GameStateManager gameStateManager;
    private List<Morpheme> availableMorphemes;
    private JButton[] morphemeButtons;

    private JPanel gridPanel;
    private JPanel constructedWordPanel;
    private JLabel scoreLabel;
    private JList<String> constructedWordsList;
    private DefaultListModel<String> constructedWordsModel;

    public MainGameFrame(User user, GameController gameController, GameStateManager gameStateManager) {
        this.currentUser = user;
        this.gameController = gameController;
        this.gameStateManager = gameStateManager;

        // Register as a listener for game state changes
        gameStateManager.addListener(this);

        setTitle("WordCrafter - Mode de jeu principal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadMorphemes();
        initComponents();
    }

    private void loadMorphemes() {
        availableMorphemes = gameController.getAvailableMorphemes();
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

        morphemeButtons = new JButton[Math.min(9, availableMorphemes.size())];

        for (int i = 0; i < morphemeButtons.length; i++) {
            Morpheme morpheme = availableMorphemes.get(i);
            JButton morphemeButton = createMorphemeButton(morpheme, i);
            morphemeButtons[i] = morphemeButton;
            gridPanel.add(morphemeButton);
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Right panel - info and constructed words list
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(LIGHT_CLOUD);
        rightPanel.setPreferredSize(new Dimension(200, 0));

        scoreLabel = new JLabel("Score: " + gameStateManager.getCurrentScore());
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
        clearButton.addActionListener(e -> gameController.handleClearSelection());
        buttonPanel.add(clearButton);
        return buttonPanel;
    }

    private JButton createMorphemeButton(Morpheme morpheme, int index) {
        JButton button = new JButton("<html><center>" + morpheme.text() + "<br><small>" +
                                     morpheme.definition() + "</small></center></html>");
        button.setBackground(MORPHEME_COLOR);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PASTEL_PINK, 2));

        button.addActionListener(e -> {
            gameController.handleMorphemeToggle(morpheme);
            // Update button appearance based on selection state
            button.setBackground(gameStateManager.isMorphemeSelected(morpheme) ? SELECTED_COLOR : MORPHEME_COLOR);
        });

        return button;
    }

    private void updateConstructedWordDisplay() {
        constructedWordPanel.removeAll();
        List<Morpheme> selected = gameStateManager.getSelectedMorphemes();

        for (int i = 0; i < selected.size(); i++) {
            Morpheme morpheme = selected.get(i);
            JLabel label = new JLabel(morpheme.text());
            label.setFont(new Font("SansSerif", Font.BOLD, 18));
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Make the label clickable to delete the morpheme
            final Morpheme toRemove = morpheme;
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    gameController.handleMorphemeToggle(toRemove);
                }
            });
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            constructedWordPanel.add(label);

            if (i < selected.size() - 1) {
                constructedWordPanel.add(new JLabel(" + "));
            }
        }

        constructedWordPanel.revalidate();
        constructedWordPanel.repaint();
    }

    private void updateGridButtons() {
        for (int i = 0; i < morphemeButtons.length; i++) {
            JButton button = morphemeButtons[i];
            Morpheme morpheme = availableMorphemes.get(i);
            button.setBackground(gameStateManager.isMorphemeSelected(morpheme) ? SELECTED_COLOR : MORPHEME_COLOR);
        }
    }

    private void checkWord() {
        List<Morpheme> selected = gameStateManager.getSelectedMorphemes();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord sélectionner des morphèmes !",
                                        "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use controller to validate word - handles all business logic
        Word validWord = gameController.handleWordVerification();

        if (validWord != null) {
            // The word is valid
            constructedWordsModel.addElement(validWord.text() + " (" + validWord.points() + " pts)");

            JOptionPane.showMessageDialog(this,
                "Mot valide ! Vous avez gagné " + validWord.points() + " points !",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            gameController.handleClearSelection();
        } else {
            StringBuilder wordText = new StringBuilder();
            for (Morpheme m : selected) {
                wordText.append(m.text());
            }
            JOptionPane.showMessageDialog(this,
                "Mot invalide : " + wordText,
                "Invalide", JOptionPane.ERROR_MESSAGE);

            // Clear selection when word is invalid
            gameController.handleClearSelection();
        }
    }

    @Override
    public void onGameStateChanged(GameStateManager.GameStateEvent event) {
        switch (event.type()) {
            case MORPHEME_SELECTED:
            case MORPHEME_DESELECTED, SELECTION_CLEARED, ROUND_RESET:
                updateConstructedWordDisplay();
                updateGridButtons();
                break;
            case WORD_CONSTRUCTED:
                updateGridButtons();
                scoreLabel.setText("Score: " + gameStateManager.getCurrentScore());
                break;
            case SCORE_UPDATED:
                scoreLabel.setText("Score: " + gameStateManager.getCurrentScore());
                break;
        }
    }
}

