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
        window = new BasicWindow("World Hub");
        window.setHints(List.of(Window.Hint.CENTERED));

        // Outer root panel to center everything
        Panel outer = new Panel();
        outer.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        outer.setPreferredSize(new TerminalSize(70, 12)); // Optional, adjust as needed

        // Add vertical spacing
        outer.addComponent(new EmptySpace());

        // Inner panel to hold content centered horizontally
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        root.setPreferredSize(new TerminalSize(60, 10));

        /* ---------- INFO Column ---------- */
        Panel infoInner = new Panel(new LinearLayout(Direction.VERTICAL));
        infoInner.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        infoInner.addComponent(textBlock("Name", player.getName()));
        infoInner.addComponent(textBlock("Gold", String.valueOf(player.getGold())));
        infoInner.addComponent(textBlock("Mission", GameState.get().getMissionFlag() != null
                ? GameState.get().getMissionFlag().toString()
                : "—"));

        Panel infoPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        infoPanel.setPreferredSize(new TerminalSize(20, 10));
        infoPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        infoPanel.addComponent(new EmptySpace());
        infoPanel.addComponent(infoInner);
        infoPanel.addComponent(new EmptySpace());

        Component borderedInfo = withBorder("Info", infoPanel);

        /* ---------- MENU Column ---------- */
        Panel menuInner = new Panel(new LinearLayout(Direction.VERTICAL));
        menuInner.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        menuInner.addComponent(new Button("Continue Story", () -> {
            window.close();
            SceneManager.get().switchTo(new Story(gui));
        }));
        menuInner.addComponent(new Button("Explore (Random Battle)", () -> {
            window.close();
            SceneManager.get().switchTo(new Exploration((MultiWindowTextGUI) gui, player));
        }));
        menuInner.addComponent(new Button("Visit Shop", () -> {
            window.close();
            SceneManager.get().switchTo(new Shop(gui, player));
        }));
        menuInner.addComponent(new Button("Character Overview", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));
        menuInner.addComponent(new Button("Save Game", () -> {
            GameState.get().getPlayer().rebuildSpellsFromIds();
            GameState.get().getPlayer().rebuildConsumablesFromIds();
            boolean success = GameState.get().saveToFile();
            MessageDialog.showMessageDialog(gui, "Save Game",
                    success ? "Game saved successfully!" : "Failed to save game.");
        }));
        menuInner.addComponent(new Button("Exit to Main Menu", () -> {
            window.close();
            SceneManager.get().switchTo(new MainMenu((MultiWindowTextGUI) gui));
        }));

        Panel menuButtons = new Panel(new LinearLayout(Direction.VERTICAL));
        menuButtons.setPreferredSize(new TerminalSize(40, 10));
        menuButtons.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        menuButtons.addComponent(new EmptySpace()); // Top spacing
        menuButtons.addComponent(menuInner);
        menuButtons.addComponent(new EmptySpace()); // Bottom spacing

        Component borderedMenu = withBorder("Menu", menuButtons);


        root.addComponent(borderedInfo);
        root.addComponent(borderedMenu);

        // Add root to outer, with vertical alignment
        outer.addComponent(root);
        outer.addComponent(new EmptySpace()); // Bottom spacing

        window.setComponent(outer);
        gui.addWindowAndWait(window);

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
