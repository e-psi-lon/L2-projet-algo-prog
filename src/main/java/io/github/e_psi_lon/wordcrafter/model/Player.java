package io.github.e_psi_lon.wordcrafter.model;

public class Player extends User {
    private final int score;
    public Player(int id, String username, String passwordHash, int score) {
        super(id, username, passwordHash);
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
