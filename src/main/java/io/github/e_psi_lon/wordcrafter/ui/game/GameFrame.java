package io.github.e_psi_lon.wordcrafter.ui.game;

import io.github.e_psi_lon.wordcrafter.controller.GameController;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.service.GameStateManager;
import io.github.e_psi_lon.wordcrafter.ui.AppColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Base class for game frames providing common UI elements and game state management.
 */
public abstract class GameFrame extends JFrame {
    protected static final Color PASTEL_PINK = AppColors.PASTEL_PINK;
    protected static final Color LIGHT_CLOUD = AppColors.LIGHT_CLOUD;
    protected static final Color BUTTON_COLOR = AppColors.BUTTON_COLOR;
    protected static final Color MORPHEME_COLOR = AppColors.MORPHEME_COLOR;
    protected static final Color SELECTED_COLOR = AppColors.SELECTED_COLOR;

    protected final GameController gameController;
    protected final GameStateManager gameStateManager;
    protected List<Morpheme> availableMorphemes;
    protected JPanel constructedWordPanel;
    protected JLabel scoreLabel;

    protected GameFrame(GameController gameController, GameStateManager gameStateManager, String title) {
        this.gameController = gameController;
        this.gameStateManager = gameStateManager;

        setTitle(title);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected void loadMorphemes() {
        availableMorphemes = gameController.getAvailableMorphemes();
    }

    protected void updateConstructedWordDisplay() {
        constructedWordPanel.removeAll();
        List<io.github.e_psi_lon.wordcrafter.model.Morpheme> selected = gameStateManager.getSelectedMorphemes();

        for (io.github.e_psi_lon.wordcrafter.model.Morpheme morpheme : selected) {
            JLabel label = new JLabel(morpheme.text());
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            constructedWordPanel.add(label);
        }

        constructedWordPanel.revalidate();
        constructedWordPanel.repaint();
    }
}
