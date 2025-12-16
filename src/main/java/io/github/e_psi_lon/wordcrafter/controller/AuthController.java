package io.github.e_psi_lon.wordcrafter.controller;

import io.github.e_psi_lon.wordcrafter.model.User;
import io.github.e_psi_lon.wordcrafter.service.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    public User handleLogin(String username, String password) {
        return authService.authenticate(username, password);
    }

    public boolean handlePlayerRegistration(String username, String password) {
        return authService.registerPlayer(username, password);
    }

    public boolean handleAdminCreation(String username, String password) {
        return authService.createAdmin(username, password);
    }
}

