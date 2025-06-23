import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import items.ItemRegistry;
import scenes.menu.MainMenu;
import scenes.manager.SceneManager;
import scenes.ui.DevLogOverlay;

import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, ex) -> ex.printStackTrace());

            // ✅ Set up terminal emulator (works on all platforms)
            Font font = new Font("Consolas", Font.PLAIN, 18);
            SwingTerminalFontConfiguration fontConfig =
                    SwingTerminalFontConfiguration.newInstance(font);

            DefaultTerminalFactory factory = new DefaultTerminalFactory()
                    .setTerminalEmulatorFontConfiguration(fontConfig)
                    .setPreferTerminalEmulator(true)
                    .setTerminalEmulatorTitle("Text Adventure");

            Terminal terminal = factory.createTerminal();
            Screen screen = factory.createScreen();
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

            // ✅ Setup game
            ItemRegistry.loadAllItems();
            DevLogOverlay.attach(gui);
            SceneManager.get().switchTo(new MainMenu(gui));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
