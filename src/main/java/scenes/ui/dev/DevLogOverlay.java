package scenes.ui.dev;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;


public class DevLogOverlay {
    @Getter
    private static final Window devLogWindow = new BasicWindow();
    private static final List<String> buffer = new LinkedList<>();
    @Getter
    private static TextBox logBox;

    public static void attach(WindowBasedTextGUI gui) {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label(" Developer Log"));

        logBox = new TextBox(new TerminalSize(80, 10), TextBox.Style.MULTI_LINE);
        logBox.setReadOnly(true);
        logBox.setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
        panel.addComponent(logBox);

        devLogWindow.setComponent(panel);
        devLogWindow.setHints(List.of(Window.Hint.NO_DECORATIONS, Window.Hint.FIXED_POSITION));
        devLogWindow.setPosition(new TerminalPosition(0, 0));

        gui.addWindow(devLogWindow);
    }

    public static void log(String msg) {
        synchronized (buffer) {
            buffer.add(msg);
            String fullText = String.join("\n", buffer);
            logBox.setText(fullText);
        }
    }

    public static void clearLog() {
        synchronized (buffer) {
            buffer.clear();
        }
    }

}

