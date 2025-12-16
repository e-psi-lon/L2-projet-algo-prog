package io.github.e_psi_lon.wordcrafter.service;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.Word;

import java.util.List;

public class GameService {
    private final DatabaseManager databaseManager;

    public GameService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Morpheme> getAllAvailableMorphemes() {
        return databaseManager.getAllMorphemes();
    }

    public Word validateWord(String wordText, List<Integer> morphemeIds) {
        return databaseManager.validateWord(wordText, morphemeIds);
    }

    public void recordPlayerWord(int userId, int wordId) {
        databaseManager.addPlayerWord(userId, wordId);
    }

    public void addMorpheme(String text, String definition) {
        databaseManager.addMorpheme(text, definition);
    }

    public void addWord(String text, List<Integer> morphemeIds, int points) {
        databaseManager.addWord(text, morphemeIds, points);
    }


    public String constructWordText(List<Morpheme> selectedMorphemes) {
        StringBuilder wordText = new StringBuilder();
        for (Morpheme morpheme : selectedMorphemes) {
            wordText.append(morpheme.text());
        }
        return wordText.toString();
    }

    public List<Integer> extractMorphemeIds(List<Morpheme> morphemes) {
        return morphemes.stream()
            .map(Morpheme::getId)
            .toList();
    }
}

