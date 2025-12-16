package io.github.e_psi_lon.wordcrafter.controller;

import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.Word;
import io.github.e_psi_lon.wordcrafter.service.GameService;
import io.github.e_psi_lon.wordcrafter.service.GameStateManager;
import io.github.e_psi_lon.wordcrafter.service.PlayerService;

import java.util.List;

public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameStateManager gameStateManager;

    public GameController(GameService gameService, PlayerService playerService, GameStateManager gameStateManager) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.gameStateManager = gameStateManager;
    }

    public void handleMorphemeToggle(Morpheme morpheme) {
        gameStateManager.toggleMorphemeSelection(morpheme);
    }

    public Word handleWordVerification() {
        List<Morpheme> selected = gameStateManager.getSelectedMorphemes();
        if (selected.isEmpty()) {
            return null;
        }

        String wordText = gameService.constructWordText(selected);
        List<Integer> morphemeIds = gameService.extractMorphemeIds(selected);

        Word validatedWord = gameService.validateWord(wordText, morphemeIds);

        if (validatedWord != null) {
            // Record the word in game state and database
            gameStateManager.recordConstructedWord(validatedWord);
            gameService.recordPlayerWord(gameStateManager.getCurrentPlayer().getId(), validatedWord.id());

            // Update player's score in the database
            playerService.addScore(gameStateManager.getCurrentPlayer().getId(), validatedWord.points());
        }

        return validatedWord;
    }

    public void handleClearSelection() {
        gameStateManager.clearSelection();
    }

    public List<Morpheme> getAvailableMorphemes() {
        return gameService.getAllAvailableMorphemes();
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }
}

