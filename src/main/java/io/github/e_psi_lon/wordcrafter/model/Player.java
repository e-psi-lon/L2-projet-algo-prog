package io.github.e_psi_lon.wordcrafter.model;

public class Player extends User {
    private final PlayerScore playerScore;

    public Player(int id, String username, String passwordHash, int score) {
        super(id, username, passwordHash);
        this.playerScore = new PlayerScore(id, score);
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

    public Player addPoints(int additionalPoints) {
        return withScore(getScore() + additionalPoints);
    }
}
