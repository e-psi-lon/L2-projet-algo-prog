package io.github.e_psi_lon.wordcrafter.model;

/**
 * Represents a user in the system (player or administrator/editor).
 */
public abstract class User implements DatabaseEntity {
    private final int id;
    private final String username;
    private final String passwordHash;


    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
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
}

