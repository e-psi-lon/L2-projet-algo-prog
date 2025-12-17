package io.github.e_psi_lon.wordcrafter.service;

import io.github.e_psi_lon.wordcrafter.controller.AuthController;
import io.github.e_psi_lon.wordcrafter.controller.EditorController;
import io.github.e_psi_lon.wordcrafter.controller.GameController;
import io.github.e_psi_lon.wordcrafter.database.DatabaseManager;
import io.github.e_psi_lon.wordcrafter.model.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public class ServiceFactory {
    private final DatabaseManager databaseManager;
    private final GameService gameService;
    private final PlayerService playerService;
    private final AuthService authService;

    private ServiceFactory(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.gameService = new GameService(databaseManager);
        this.playerService = new PlayerService(databaseManager);
        this.authService = new AuthService(databaseManager);
    }

    @Contract(" -> new")
    public static @NotNull ServiceFactory initialize() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initialize();
        return new ServiceFactory(dbManager);
    }

    public GameController createGameController(Player player) {
        GameStateManager stateManager = new GameStateManager(player);
        return new GameController(gameService, playerService, stateManager);
    }

    public AuthController createAuthController() {
        return new AuthController(authService);
    }

    public EditorController createEditorController() {
        return new EditorController(gameService, createAuthController());
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public GameService getGameService() {
        return gameService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void shutdown() {
        databaseManager.close();
    }
}

