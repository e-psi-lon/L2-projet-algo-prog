package io.github.e_psi_lon.wordcrafter.model;

/**
 * Represents a morpheme in the game.
 */
public record Morpheme(int id, String text, String definition) implements DatabaseEntity {

    public int getId() {
        return id();
    }

    @Override
    public String toString() {
        return text;
    }
}


