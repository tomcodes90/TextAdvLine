package util;

import scenes.ui.DevLogOverlay;

/**
 * =======================================================
 * DeveloperLogger
 * =======================================================
 * <p>
 * Lightweight utility class for developer debugging messages.
 * It logs messages to the DevLogOverlay window *if it is initialized*.
 * <p>
 * - Does nothing if the overlay is not available.
 * - Designed to be safe in production (no crashes).
 */
public final class DeveloperLogger {

    /**
     * Logs a message to the DevLogOverlay, if the overlay is present.
     *
     * @param msg The debug message to be shown
     */
    public static void log(String msg) {
        // Only attempt to log if the developer overlay exists
        if (DevLogOverlay.getLogBox() != null) {
            DevLogOverlay.log(msg);
        }
    }

    // Private constructor to prevent instantiation (utility class)
    private DeveloperLogger() {
    }
}
