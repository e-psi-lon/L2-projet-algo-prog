package io.github.e_psi_lon.wordcrafter.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Player extends User {
    private final PlayerScore playerScore;

    public Player(int id, String username, String passwordHash, int score) {
        this(id, username, passwordHash, new PlayerScore(id, score));
    }

    private Player(int id, String username, String passwordHash, @NotNull PlayerScore playerScore) {
        super(id, username, passwordHash);
        this.playerScore = playerScore;
    }



    public int getScore() {
        return playerScore.getPoints();
    }

    public PlayerScore getPlayerScore() {
        return playerScore;
    }

    public Player withScore(int newScore) {
        return new Player(getId(), getUsername(), getPasswordHash(), newScore);
    }
    @Contract("_ -> new")
    private @NotNull Player withScore(@NotNull PlayerScore newScore) {
        return new Player(getId(), getUsername(), getPasswordHash(), newScore);
    }

    public Player addPoints(int additionalPoints) {
        return withScore(getPlayerScore().withAdditionalPoints(additionalPoints));
    }
}
