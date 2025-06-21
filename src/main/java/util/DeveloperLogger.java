package util;

import ui.DevLogOverlay;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public final class DeveloperLogger {
    private static final Logger LOG = Logger.getLogger("Developer");

    static {
        try {
            new File("logs").mkdirs();
            FileHandler fh = new FileHandler("logs/dev.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);

            LOG.setUseParentHandlers(false);
            LOG.setLevel(Level.FINE);
        } catch (IOException e) {
            System.err.println("Failed to initialize DeveloperLogger: " + e.getMessage());
        }
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void fine(String msg) {
        log(Level.FINE, msg);
    }

    public static void warn(String msg) {
        log(Level.WARNING, msg);
    }

    public static void log(Level level, String msg) {
        LOG.log(level, msg);
        DevLogOverlay.log(msg);  // Push to overlay
    }

    public static void log(String msg) {
        DevLogOverlay.log(msg);  // Push to overlay
    }
}
