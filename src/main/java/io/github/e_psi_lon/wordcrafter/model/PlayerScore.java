package io.github.e_psi_lon.wordcrafter.model;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record PlayerScore(int userId, int points) {

    public int getUserId() {
        return userId;
    }

    public int getPoints() {
        return points;
    }

    @Contract("_ -> new")
    public @NotNull PlayerScore withAdditionalPoints(int additionalPoints) {
        return new PlayerScore(userId, points + additionalPoints);
    }
}

