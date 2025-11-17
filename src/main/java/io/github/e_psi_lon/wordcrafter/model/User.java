package io.github.e_psi_lon.wordcrafter.model;

/**
 * Represents a user in the system (player or administrator/editor).
 */
public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final UserRole role;
    private int score;

    public enum UserRole {
        PLAYER,
        ADMIN
    }

    public User(int id, String username, String passwordHash, UserRole role, int score) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}

