package io.github.e_psi_lon.wordcrafter.service;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;

public class PlayerService {
    private final DatabaseManager databaseManager;

    public PlayerService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addScore(int userId, int additionalPoints) {
        databaseManager.updateUserScore(userId, additionalPoints);
    }
}

