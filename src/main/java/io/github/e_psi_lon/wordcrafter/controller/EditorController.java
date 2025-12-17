package io.github.e_psi_lon.wordcrafter.controller;

import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.Word;
import io.github.e_psi_lon.wordcrafter.service.GameService;

import java.util.List;

/**
 * Controller for editor/admin-related actions.
 * Orchestrates communication between Editor UI and Game Service.
 */
public class EditorController {
    private final GameService gameService;
    private final AuthController authController;

    public EditorController(GameService gameService, AuthController authController) {
        this.gameService = gameService;
        this.authController = authController;
    }

    public void handleAddMorpheme(String text, String definition) {
        gameService.addMorpheme(text, definition);
    }

    public void handleAddWord(String text, List<Integer> morphemeIds, int points, String definition) {
        gameService.addWord(text, morphemeIds, points, definition);
    }

    public boolean handleCreateAdmin(String username, String password) {
        return authController.handleAdminCreation(username, password);
    }

    public List<Morpheme> getAllMorphemes() {
        return gameService.getAllAvailableMorphemes();
    }

    public List<Word> getAllWords() {
        return gameService.getAllAvailableWords();
    }
}

