package util;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextBox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * =======================================================
 * PlayerLogger
 * =======================================================
 * <p>
 * Utility class for logging text to the screen with a
 * "typewriter" animation effect. Meant for displaying
 * narrative or dialogue messages to the player.
 * <p>
 * - Uses a separate thread to animate typing.
 * - Can be used non-blocking (`log()`) or blocking (`logBlocking()`).
 * - Requires a TextBox and GUI reference to work.
 */
public final class PlayerLogger {

    // The text box where logs are displayed
    private static TextBox logBox;

    // Optional refresh callback (re-renders the screen)
    private static Runnable refresher;

    // Reference to the main GUI thread (needed to update Lanterna safely)
    private static MultiWindowTextGUI gui;

    // Single-threaded executor to simulate typing one message at a time
    private static final ExecutorService typer = Executors.newSingleThreadExecutor(r ->
            new Thread(r, "typewriter"));

    // Delay between letters (ms)
    private static final int LETTER_DELAY = 25;

    // Delay after each message is fully typed (ms)
    private static final int MESSAGE_DELAY = 700;

    /* ------------------------------------------------------------------
     * Initializes the PlayerLogger
     * This must be called before logging anything.
     *
     * @param box      the TextBox where text will appear
     * @param guiRef   reference to the GUI system
     * @param refresh  optional UI refresh runnable
     */
    public static void init(TextBox box, MultiWindowTextGUI guiRef, Runnable refresh) {
        logBox = box;
        gui = guiRef;
        refresher = refresh;
    }

    /**
     * Log a message with typewriter effect (non-blocking).
     */
    public static void log(String msg) {
        typer.submit(() -> typeWriter(msg));
    }

    /**
     * Log a message and wait for it to finish typing (blocking).
     */
    public static void logBlocking(String msg) {
        try {
            typer.submit(() -> typeWriter(msg)).get();  // Waits for typing to complete
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs the typewriter animation letter by letter.
     */
    private static void typeWriter(String line) {
        StringBuilder message = new StringBuilder();

        for (char c : line.toCharArray()) {
            message.append(c);
            updateUI(message.toString()); // update after each letter
            sleep(LETTER_DELAY);          // small delay between letters
        }

        updateUI(message.toString());     // final update
        sleep(MESSAGE_DELAY);            // pause after message
    }

    /**
     * Updates the Lanterna UI safely from background thread.
     */
    private static void updateUI(String text) {
        if (gui == null || logBox == null) return;

        gui.getGUIThread().invokeLater(() -> {
            logBox.setText(text);                // set full message
            logBox.setCaretPosition(text.length()); // move caret to end
            if (refresher != null) refresher.run(); // optional refresh
        });
    }

    /**
     * Utility method to sleep safely in milliseconds.
     */
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt(); // Restore interrupt flag
        }
    }
}
