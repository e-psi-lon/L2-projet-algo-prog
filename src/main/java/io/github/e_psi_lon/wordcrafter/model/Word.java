package io.github.e_psi_lon.wordcrafter.model;

import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * Represents a valid word in the game.
 */
public record Word(int id, String text, List<Integer> morphemeIds, int points) implements DatabaseEntity {

    public int getId() {
        return id();
    }

    @Override
    @NotNull
    public String toString() {
        return text;
    }
}

