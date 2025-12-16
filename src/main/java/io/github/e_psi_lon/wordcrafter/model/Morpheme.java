package io.github.e_psi_lon.wordcrafter.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a morpheme in the game.
 */
public record Morpheme(int id, String text, String definition) implements DatabaseEntity {

    public int getId() {
        return id();
    }

    @Override
    @NotNull
    public String toString() {
        return text;
    }
}


