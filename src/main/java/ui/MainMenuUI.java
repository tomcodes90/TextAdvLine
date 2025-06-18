package ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.util.List;

public class MainMenuUI {

    public void show(WindowBasedTextGUI gui) {
        // === BACKGROUND FULLSCREEN WINDOW ===
        BasicWindow bgWindow = new BasicWindow();
        bgWindow.setHints(List.of(Window.Hint.FULL_SCREEN));
        Panel bgPanel = new Panel();
        bgPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        bgPanel.setTheme(new SimpleTheme(TextColor.ANSI.BLACK, TextColor.ANSI.CYAN));
        bgWindow.setComponent(bgPanel);
        gui.addWindow(bgWindow);

        // === MAIN WINDOW (logo + menu) ===
        BasicWindow mainWindow = new BasicWindow();
        mainWindow.setHints(List.of(Window.Hint.CENTERED));

        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.setPreferredSize(new TerminalSize(90, 20));

        // === ASCII LOGO ===
        String logo = """
                                                (                                            
                 (       (                      )\\ )                            )            
                 )\\ )    )\\      (   (      )  (()/(   (   (             (   ( /(   (      ) 
                (()/(   ((_) (   )(  )\\  ( /(   /(_)) ))\\  )(   `  )    ))\\  )\\()) ))\\  ( /( 
                 /(_))_  _   )\\ (()\\((_) )(_)) (_))  /((_)(()\\  /(/(   /((_)(_))/ /((_) )(_))
                (_)) __|| | ((_) ((_)(_)((_)_  | _ \\(_))   ((_)((_)_\\ (_))  | |_ (_))( ((_)_ 
                  | (_ || |/ _ \\| '_|| |/ _` | |  _// -_) | '_|| '_ \\)/ -_) |  _|| || |/ _` |
                   \\___||_|\\___/|_|  |_|\\__,_| |_|  \\___| |_|  | .__/ \\___|  \\__| \\_,_|\\__,_|
                                                               |_|                           
""";

        Label logoLabel = new Label(logo)
                .addStyle(SGR.BOLD)
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        mainPanel.addComponent(logoLabel);

        // Spacer
        mainPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        // === MENU ===
        ActionListBox menu = new ActionListBox(new TerminalSize(30, 5));
        menu.addItem("> Start Game", () -> {
            mainWindow.close();
            bgWindow.close();
            MessageDialog.showMessageDialog(gui, "Game Start", "Starting the game!", MessageDialogButton.OK);
        });
        menu.addItem("Load Game", () -> {
            MessageDialog.showMessageDialog(gui, "Load Game", "Feature coming soon!", MessageDialogButton.OK);
        });
        menu.addItem("Exit", () -> {
            mainWindow.close();
            bgWindow.close();
        });

        mainPanel.addComponent(menu);

        // Set panel in window and show
        mainWindow.setComponent(mainPanel);
        gui.addWindowAndWait(mainWindow);
    }
}
