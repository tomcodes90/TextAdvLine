import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import ui.MainMenuUI;

public class Main {
    public static void main(String[] args) {
        try {
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            new MainMenuUI().show(gui);

            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
