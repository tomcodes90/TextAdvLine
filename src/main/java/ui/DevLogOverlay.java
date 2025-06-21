package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.LinkedList;
import java.util.List;

public class DevLogOverlay {
    private static final Window devLogWindow = new BasicWindow();
    private static final List<String> buffer = new LinkedList<>();
    private static TextBox logBox;

    public static void attach(WindowBasedTextGUI gui) {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label(" Developer Log"));

        logBox = new TextBox(new TerminalSize(80, 10), TextBox.Style.MULTI_LINE);
        logBox.setReadOnly(true);
        panel.addComponent(logBox);

        devLogWindow.setComponent(panel);
        devLogWindow.setHints(List.of(Window.Hint.NO_DECORATIONS, Window.Hint.FIXED_POSITION));
        devLogWindow.setPosition(new TerminalPosition(0, 0));

        gui.addWindow(devLogWindow);
    }

    public static void log(String msg) {
        synchronized (buffer) {
            buffer.add(msg);
            if (buffer.size() > 15) buffer.remove(0);
            logBox.setText(String.join("\n", buffer));
        }
    }
}

