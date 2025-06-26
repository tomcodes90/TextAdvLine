package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.missions.Tutorial;
import scenes.worldhub.WorldHub;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

public class MainMenu implements Scene {
    private final Window window = new BasicWindow("Main Menu");
    private final MultiWindowTextGUI gui;

    public MainMenu(MultiWindowTextGUI gui) {
        this.gui = gui;
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        window.setHints(List.of(Window.Hint.CENTERED));
        panel.addComponent(new Label("Welcome to the Main Menu"));
        panel.addComponent(new Button("Start Game", () -> {
            DeveloperLogger.log("Start Game Clicked");
            SceneManager.get().switchTo(new Tutorial(gui));
            window.close();// or switch scene
        }));
        // or switch scene
        panel.addComponent(new Button("Load Game", () -> {
            if (GameState.loadInstance()) {
                Player p = GameState.get().getPlayer();
                p.rebuildSpellsFromIds();
                p.rebuildConsumablesFromIds();
                SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer()));
            }
        }));

        panel.addComponent(new Button("Options", () -> {
            DeveloperLogger.log("Start Game Clicked");// or switch scene
        }));
        panel.addComponent(new Button("Exit", () -> {
            DeveloperLogger.log("Start Game Clicked");// or switch scene
        }));


        window.setComponent(panel);
    }

    @Override
    public void enter() {
        gui.addWindowAndWait(window); // This keeps the app alive
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {

    }
}

