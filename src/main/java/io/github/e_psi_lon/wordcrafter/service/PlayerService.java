package io.github.e_psi_lon.wordcrafter.service;

import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.Player;

public class PlayerService {
    private final DatabaseManager databaseManager;

    public PlayerService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void addScore(int userId, int additionalPoints) {
        databaseManager.updateUserScore(userId, additionalPoints);
    }

    public int getPlayerScore(Player player) {
        return player.getScore();
    }

    public int calculatePlayerTotalScore(Player player) {
        return player.getScore();
    }
}

