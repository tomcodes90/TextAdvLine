import items.ItemRegistry;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import scenes.MainMenu;
import scenes.SceneManager;
import scenes.ui.DevLogOverlay;

import java.io.IOException;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;

public class Main {
    public static void main(String[] args) {

        try {
            Thread.setDefaultUncaughtExceptionHandler((t, ex) -> ex.printStackTrace());
            Screen screen = new TerminalScreen(new DefaultTerminalFactory().createTerminal());
            screen.startScreen();
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            ItemRegistry.loadAllItems();
            DevLogOverlay.attach(gui);
            SceneManager.get().switchTo(new MainMenu(gui));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


