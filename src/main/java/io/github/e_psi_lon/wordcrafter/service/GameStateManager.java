package io.github.e_psi_lon.wordcrafter.service;

import io.github.e_psi_lon.wordcrafter.model.Morpheme;
import io.github.e_psi_lon.wordcrafter.model.Player;
import io.github.e_psi_lon.wordcrafter.model.Word;

import java.util.*;

public class GameStateManager {
    private final Player currentPlayer;
    private final List<Morpheme> selectedMorphemes;
    private final List<Word> constructedWords;
    private final List<GameStateListener> listeners;
    private int currentScore;

    public GameStateManager(Player player) {
        this.currentPlayer = player;
        this.selectedMorphemes = new ArrayList<>();
        this.constructedWords = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.currentScore = player.getScore();
    }

    public void addListener(GameStateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(GameStateEvent event) {
        for (GameStateListener listener : listeners) {
            listener.onGameStateChanged(event);
        }
    }

    public void toggleMorphemeSelection(Morpheme morpheme) {
        if (selectedMorphemes.contains(morpheme)) {
            selectedMorphemes.remove(morpheme);
            notifyListeners(new GameStateEvent(GameStateEvent.Type.MORPHEME_DESELECTED, morpheme));
        } else {
            selectedMorphemes.add(morpheme);
            notifyListeners(new GameStateEvent(GameStateEvent.Type.MORPHEME_SELECTED, morpheme));
        }
    }

    public boolean isMorphemeSelected(Morpheme morpheme) {
        return selectedMorphemes.contains(morpheme);
    }

    public List<Morpheme> getSelectedMorphemes() {
        return new ArrayList<>(selectedMorphemes);
    }

    public void clearSelection() {
        selectedMorphemes.clear();
        notifyListeners(new GameStateEvent(GameStateEvent.Type.SELECTION_CLEARED, null));
    }

    public void recordConstructedWord(Word word) {
        constructedWords.add(word);
        currentScore += word.points();
        notifyListeners(new GameStateEvent(GameStateEvent.Type.WORD_CONSTRUCTED, word));
    }

    public List<Word> getConstructedWords() {
        return new ArrayList<>(constructedWords);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentScore() {
        return currentScore;
    }


    public void updateScore(int newScore) {
        int oldScore = currentScore;
        this.currentScore = newScore;
        if (oldScore != newScore) {
            notifyListeners(new GameStateEvent(GameStateEvent.Type.SCORE_UPDATED, newScore));
        }
    }

    public void resetForNewRound() {
        clearSelection();
        notifyListeners(new GameStateEvent(GameStateEvent.Type.ROUND_RESET, null));
    }

    public record GameStateEvent(Type type, Object data) {
        public enum Type {
            MORPHEME_SELECTED,
            MORPHEME_DESELECTED,
            SELECTION_CLEARED,
            WORD_CONSTRUCTED,
            SCORE_UPDATED,
            ROUND_RESET
        }
    }
}

