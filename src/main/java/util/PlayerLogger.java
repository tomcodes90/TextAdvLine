package util;

import com.googlecode.lanterna.gui2.TextBox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public final class PlayerLogger {
    private static final Logger LOG = Logger.getLogger("player");

    /**
     * the TextBox on-screen that shows the battle log
     */
    private static TextBox logBox;
    /**
     * run() → gui.getGUIThread().invokeLater(refreshSafe)
     */
    private static Runnable refresher;

    /**
     * background executor that “types” characters
     */
    private static final ExecutorService typer = Executors.newSingleThreadExecutor(r ->
            new Thread(r, "typewriter"));

    /**
     * milliseconds between characters
     */
    private static final int LETTER_DELAY = 25;
    private static final int MESSAGE_DELAY = 700;

    /* ------------------------------------------------------------------ */
    /*  static init – hook Java util.logging to our TextBox               */
    /* ------------------------------------------------------------------ */
    static {
        LOG.setUseParentHandlers(false);
        LOG.setLevel(Level.ALL);

        LOG.addHandler(new Handler() {
            @Override
            public void publish(LogRecord rec) {
                if (logBox == null) return;     // not initialised yet

                // Every log line is rendered with a type-writer effect
                String msg = rec.getMessage();
                typer.submit(() -> typeWriter(msg));
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
    }

    /* ------------------------------------------------------------------ */
    /*  public helpers                                                     */
    /* ------------------------------------------------------------------ */
    public static void init(TextBox box, Runnable refresh) {
        logBox = box;
        refresher = refresh;
    }

    /**
     * ordinary call from game code
     */
    public static void log(String msg) {
        typer.submit(() -> typeWriter(msg));
    }

    public static void logBlocking(String msg) {
        try {
            typer.submit(() -> typeWriter(msg)).get();  // ensures the message finishes typing
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /* ------------------------------------------------------------------ */
    /*  INTERNAL: character-by-character writer                            */
    /* ------------------------------------------------------------------ */

    // delay after full message

    private static void typeWriter(String line) {
        StringBuilder message = new StringBuilder();

        for (char c : line.toCharArray()) {
            message.append(c);
            if (logBox != null) {
                logBox.setText(message.toString());
                logBox.setCaretPosition(message.length()); // 👈 this line scrolls to bottom
            }
            if (refresher != null) refresher.run();

            try {
                Thread.sleep(LETTER_DELAY);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        // Final repaint after full message
        if (logBox != null) {
            logBox.setText(message.toString());
            logBox.setCaretPosition(message.length()); // 👈 again after final render
        }
        if (refresher != null) refresher.run();

        try {
            Thread.sleep(MESSAGE_DELAY);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }


}
