package util;

import scenes.ui.dev.DevLogOverlay;

public final class DeveloperLogger {

    public static void log(String msg) {

        if (DevLogOverlay.getLogBox() != null) {
            DevLogOverlay.log(msg);
        }
    }
}
