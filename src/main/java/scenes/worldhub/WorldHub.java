// File: scenes/worldhub/WorldHub.java
package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.missions.Exploration;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

import static util.UIHelper.*;

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
        /* ---------- Window ---------- */
        window = new BasicWindow("World Hub");
        window.setHints(List.of(Window.Hint.CENTERED));

        /* ---------- Root (horizontal) ---------- */
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.setPreferredSize(new TerminalSize(60, 18));   // more space

        /* ---------- INFO Column ---------- */
        Panel infoPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        infoPanel.addComponent(centeredLabel("Player Info"));
        infoPanel.addComponent(textBlock("Name", player.getName()));
        infoPanel.addComponent(textBlock("Gold", String.valueOf(player.getGold())));
        infoPanel.addComponent(textBlock("Mission", GameState.get().getMissionFlag() != null
                ? GameState.get().getMissionFlag().name()
                : "—"));
        Component borderedInfo = withBorder("Info", infoPanel);

        /* ---------- MENU Column ---------- */
        Panel menuButtons = new Panel(new LinearLayout(Direction.VERTICAL));
        menuButtons.addComponent(new Button("Continue Story", () -> {
            window.close();
            SceneManager.get().switchTo(new Story(gui));   // replace if you have chapter selector
        }));
        menuButtons.addComponent(new Button("Explore (Random Battle)", () -> {
            window.close();
            SceneManager.get().switchTo(new Exploration((MultiWindowTextGUI) gui, player));
        }));
        menuButtons.addComponent(new Button("Visit Shop", () -> {
            window.close();
            SceneManager.get().switchTo(new Shop(gui, player));
        }));
        menuButtons.addComponent(new Button("Character Overview", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        menuButtons.addComponent(new Button("Save Game", () -> {
            GameState.get().getPlayer().rebuildSpellsFromIds();       // ensure arrays in sync
            GameState.get().getPlayer().rebuildConsumablesFromIds();  // (not mandatory if already synced)W
            boolean success = GameState.get().saveToFile();
            MessageDialog.showMessageDialog(gui, "Save Game",
                    success ? "Game saved successfully!" : "Failed to save game.");
        }));
        menuButtons.addComponent(new Button("Exit to Main Menu", () -> {
            window.close();
            SceneManager.get().switchTo(new MainMenu((MultiWindowTextGUI) gui));
        }));
        Component borderedMenu = withBorder("Menu", menuButtons);

        /* ---------- Assemble ---------- */
        root.addComponent(borderedInfo);
        root.addComponent(new EmptySpace(new TerminalSize(2, 1)));
        root.addComponent(borderedMenu);

        window.setComponent(root);
        gui.addWindowAndWait(window);

        // Debug log
        DeveloperLogger.log("WorldHub entered, mission flag: "
                + (GameState.get().getMissionFlag() != null
                ? GameState.get().getMissionFlag()
                : "NONE"));
    }

    @Override
    public void handleInput() { /* blocking */ }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
