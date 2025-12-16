package io.github.e_psi_lon.wordcrafter.service;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.User;


public class AuthService {
    private final DatabaseManager databaseManager;

    public AuthService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public User authenticate(String username, String password) {
        return databaseManager.authenticateUser(username, password);
    }

    public boolean registerPlayer(String username, String password) {
        return databaseManager.createPlayer(username, password);
    }

    public boolean createAdmin(String username, String password) {
        return databaseManager.createAdmin(username, password);
    }
}

