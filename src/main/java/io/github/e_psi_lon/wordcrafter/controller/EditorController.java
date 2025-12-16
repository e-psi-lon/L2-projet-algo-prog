package io.github.e_psi_lon.wordcrafter.controller;

import io.github.e_psi_lon.wordcrafter.service.GameService;

import java.util.List;

/**
 * Controller for editor/admin-related actions.
 * Orchestrates communication between Editor UI and Game Service.
 */
public class EditorController {
    private final GameService gameService;

    public EditorController(GameService gameService) {
        this.gameService = gameService;
    }

    public void handleAddMorpheme(String text, String definition) {
        gameService.addMorpheme(text, definition);
    }

    public void handleAddWord(String text, List<Integer> morphemeIds, int points, String definition) {
        gameService.addWord(text, morphemeIds, points, definition);
    }
}

