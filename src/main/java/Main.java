import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.ui.DevLogOverlay;
import util.ItemRegistry;
import util.PortraitRegistry;

import java.awt.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, ex) -> ex.printStackTrace());

            Font font = chooseMono(18);
            SwingTerminalFontConfiguration fontConfig =
                    SwingTerminalFontConfiguration.newInstance(font);

            DefaultTerminalFactory factory = new DefaultTerminalFactory()
                    .setTerminalEmulatorFontConfiguration(fontConfig)
                    .setPreferTerminalEmulator(true)
                    .setInitialTerminalSize(new TerminalSize(200, 60))
                    .setTerminalEmulatorTitle("Text Adventure");

            Terminal terminal = factory.createTerminal();

            // ⬇️ Maximize the Swing terminal window
            if (terminal instanceof SwingTerminalFrame swingTerminal) {
                swingTerminal.setExtendedState(Frame.MAXIMIZED_BOTH);
                swingTerminal.setVisible(true);
            }

            Screen screen = factory.createScreen();
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(
                    screen,
                    new DefaultWindowManager(),
                    new EmptySpace(TextColor.ANSI.WHITE_BRIGHT)
            );

            // Load content
            ItemRegistry.loadAllItems();
            PortraitRegistry.loadAllPortraits();

            // Developer overlay (optional)
            // DevLogOverlay.attach(gui);

            // Launch main menu
            SceneManager.get().switchTo(new MainMenu(gui));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Font chooseMono(float size) {
        String[] candidates = {"Consolas", "Menlo", "Monaco", "Courier New", "Monospaced"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        for (String name : candidates) {
            if (Arrays.asList(ge.getAvailableFontFamilyNames()).contains(name)) {
                return new Font(name, Font.PLAIN, (int) size);
            }
        }

        // fallback
        return new Font(Font.MONOSPACED, Font.PLAIN, (int) size);
    }
}
