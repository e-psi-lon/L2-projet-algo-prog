package io.github.e_psi_lon.wordcrafter.model;


public record PlayerScore(int userId, int points) {

    public int getUserId() {
        return userId;
    }

    public int getPoints() {
        return points;
    }

    public PlayerScore withAdditionalPoints(int additionalPoints) {
        return new PlayerScore(userId, points + additionalPoints);
    }
}

