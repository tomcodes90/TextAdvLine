import items.ItemRegistry;
import ui.GameLoopManager;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ui.DevLogOverlay;

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


            GameLoopManager loopManager = new GameLoopManager(gui);
            loopManager.showMainMenu();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


