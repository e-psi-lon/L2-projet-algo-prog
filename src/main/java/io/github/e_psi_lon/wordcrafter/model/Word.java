package io.github.e_psi_lon.wordcrafter.model;

import java.util.List;

/**
 * Represents a valid word in the game.
 */
public class Word {
    private final int id;
    private final String text;
    private final List<Integer> morphemeIds;
    private final int points;

    public Word(int id, String text, List<Integer> morphemeIds, int points) {
        this.id = id;
        this.text = text;
        this.morphemeIds = morphemeIds;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Integer> getMorphemeIds() {
        return morphemeIds;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return text;
    }
}

