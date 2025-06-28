// File: scenes/ui/DevLogOverlay.java
package scenes.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * DevLogOverlay displays a persistent developer log window at the top-left corner of the screen.
 * <p>
 * 💡 Purpose:
 * - Useful for debugging events, mission flags, or other game state transitions.
 * - Logs messages in a scrollable, read-only TextBox.
 * <p>
 * ⚠️ Notes on Lanterna and Risks:
 * - We set `NO_DECORATIONS` and `FIXED_POSITION` to keep the log minimal and static.
 * - GUI elements like TextBox must be updated on the **GUI thread**. We rely on synchronized buffer access
 * and assume log updates are called from safe contexts.
 * - `logBox.setText()` is not thread-safe on its own; wrap it in `invokeLater()` if calling from a non-UI thread.
 */
public class DevLogOverlay {
    @Getter
    private static final Window devLogWindow = new BasicWindow();

    private static final List<String> buffer = new LinkedList<>(); // Store log lines
    @Getter
    private static TextBox logBox;

    /**
     * Adds the developer log overlay to the GUI.
     * Should be called once during UI setup (e.g., in WorldHub or boot scene).
     */
    public static void attach(WindowBasedTextGUI gui) {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(" Developer Log")); // simple title

        // Create read-only multiline textbox with 80x20 dimensions
        logBox = new TextBox(new TerminalSize(80, 20), TextBox.Style.MULTI_LINE);
        logBox.setReadOnly(true);

        // Apply black background and white text
        logBox.setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
        panel.addComponent(logBox);

        // Attach panel to the window
        devLogWindow.setComponent(panel);

        // Window hints:
        // - NO_DECORATIONS: removes border and title bar
        // - FIXED_POSITION: prevents Lanterna from auto-positioning it
        devLogWindow.setHints(List.of(Window.Hint.NO_DECORATIONS, Window.Hint.FIXED_POSITION));
        devLogWindow.setPosition(new TerminalPosition(0, 0)); // Top-left corner

        // Add the window to the GUI
        gui.addWindow(devLogWindow);
    }

    /**
     * Appends a message to the log.
     * Note: not safe from non-UI threads unless you wrap the call in `invokeLater()`.
     */
    public static void log(String msg) {
        synchronized (buffer) {
            buffer.add(msg);

            // Combine all messages into a single string
            String fullText = String.join("\n", buffer);

            // Set content (not thread-safe from background threads!)
            logBox.setText(fullText);
        }
    }

    /**
     * Clears all log messages.
     */
    public static void clearLog() {
        synchronized (buffer) {
            buffer.clear();
        }
    }
}
