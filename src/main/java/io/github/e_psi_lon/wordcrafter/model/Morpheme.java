package io.github.e_psi_lon.wordcrafter.model;

/**
 * Represents a morpheme in the game.
 */
public class Morpheme {
    private final int id;
    private final String text;
    private final MorphemeType type;

    public enum MorphemeType {
        PREFIX,
        ROOT,
        SUFFIX
    }

    public Morpheme(int id, String text, MorphemeType type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public MorphemeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }
}

