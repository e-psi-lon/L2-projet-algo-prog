package io.github.e_psi_lon.wordcrafter.service;

public interface GameStateListener {
    void onGameStateChanged(GameStateManager.GameStateEvent event);
}

