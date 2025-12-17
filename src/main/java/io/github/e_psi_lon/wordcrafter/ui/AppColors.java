package io.github.e_psi_lon.wordcrafter.ui;

import java.awt.*;

/**
 * Centralized color constants for the entire application.
 * All UI components should use these colors for consistency.
 */
public final class AppColors {
    private AppColors() {
        throw new AssertionError("AppColors should not be instantiated");
    }
    public static final Color PASTEL_PINK = new Color(255, 209, 220);
    public static final Color LIGHT_CLOUD = new Color(255, 240, 245);
    public static final Color BUTTON_COLOR = new Color(255, 182, 193);
    public static final Color MORPHEME_COLOR = new Color(255, 228, 225);
    public static final Color SELECTED_COLOR = new Color(255, 192, 203);
    public static final Color TITLE_TEXT = new Color(199, 21, 133);
    public static final Color SUCCESS_TEXT = new Color(0, 128, 0);
    public static final Color DANGER = new Color(220, 20, 60);
    public static final Color DISCONNECT = new Color(220, 100, 100);
    public static final Color NEUTRAL = new Color(200, 200, 200);
    public static final Color OVERLAY = new Color(128, 128, 128, 100);
}

