// File: scenes/MainMenuScene.java
package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.missions.Exploration;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

public class WorldHub implements Scene {
    private final WindowBasedTextGUI gui;
    private BasicWindow window;
    private final Player player;


    public WorldHub(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🌍 Main Menu");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("What would you like to do?"));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("📖 Continue Story", () -> {
            window.close();
            SceneManager.get().switchTo(new Story(gui)); // placeholder
        }));
        panel.addComponent(new Button("🧭 Explore (Random Battle)", () -> {
            window.close();
            SceneManager.get().switchTo(new Exploration((MultiWindowTextGUI) gui, player));
        }));


        panel.addComponent(new Button("🛒 Visit Shop", () -> {
            window.close();
            SceneManager.get().switchTo(new Shop(gui, player));
        }));

        panel.addComponent(new Button("🎒 Character Overview", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
            // placeholder
        }));

        panel.addComponent(new Button("💾 Save Game", () -> {
            MessageDialog.showMessageDialog(gui, "Save", "Game saved! (not really yet)");
        }));

        panel.addComponent(new Button("🏁 Exit to Main Menu", () -> {

            window.close();
            SceneManager.get().switchTo(new MainMenu((MultiWindowTextGUI) gui));
            // placeholder
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
        DeveloperLogger.log("WorldHub has been entered." + GameState.get().getMissionFlag().toString());
    }

    @Override
    public void handleInput() {
        // No polling logic needed unless you implement non-blocking input
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
