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

    /**
     * Handles adding a new morpheme to the game database.
     *
     * @param text the morpheme text
     * @param definition the French definition
     */
    public void handleAddMorpheme(String text, String definition) {
        gameService.addMorpheme(text, definition);
    }

    /**
     * Handles adding a new word to the game database.
     *
     * @param text the word text
     * @param morphemeIds the IDs of morphemes that compose this word (in order)
     * @param points the points awarded for constructing this word
     */
    public void handleAddWord(String text, List<Integer> morphemeIds, int points) {
        gameService.addWord(text, morphemeIds, points);
    }
}

