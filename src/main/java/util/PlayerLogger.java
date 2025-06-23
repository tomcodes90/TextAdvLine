package util;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextBox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PlayerLogger {

    private static TextBox logBox;
    private static Runnable refresher;
    private static MultiWindowTextGUI gui;

    private static final ExecutorService typer = Executors.newSingleThreadExecutor(r ->
            new Thread(r, "typewriter"));

    private static final int LETTER_DELAY = 25;
    private static final int MESSAGE_DELAY = 700;

    /* ------------------------------------------------------------------ */
    public static void init(TextBox box, MultiWindowTextGUI guiRef, Runnable refresh) {
        logBox = box;
        gui = guiRef;
        refresher = refresh;
    }

    public static void log(String msg) {
        typer.submit(() -> typeWriter(msg));
    }

    public static void logBlocking(String msg) {
        try {
            typer.submit(() -> typeWriter(msg)).get();  // blocks until typed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void typeWriter(String line) {
        StringBuilder message = new StringBuilder();

        for (char c : line.toCharArray()) {
            message.append(c);
            updateUI(message.toString());
            sleep(LETTER_DELAY);
        }

        updateUI(message.toString());
        sleep(MESSAGE_DELAY);
    }

    private static void updateUI(String text) {
        if (gui == null || logBox == null) return;

        gui.getGUIThread().invokeLater(() -> {
            logBox.setText(text);
            logBox.setCaretPosition(text.length());
            if (refresher != null) refresher.run();
        });
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
