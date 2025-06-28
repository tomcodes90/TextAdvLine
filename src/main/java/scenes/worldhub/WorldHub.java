// File: scenes/worldhub/WorldHub.java
package scenes.worldhub;

import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.MainMenu;
import scenes.missions.Exploration;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

import static util.UIHelper.*;

/**
 * 📍 WorldHub: The player's central screen.
 * Acts like a home base where you choose story progression, battle, shop, etc.
 * <p>
 * 🧩 Layout Overview:
 * - Two vertical sections inside a horizontally-aligned root:
 * 🔹 Info: Player stats (name, gold, mission progress)
 * 🔹 Menu: Buttons for all available actions
 * <p>
 * 🧠 Notes:
 * - Player instance is passed from GameState.
 * - Saving will serialize and store spell/item references.
 * - Boost button is here because the game lack balance, if you get stacked use it.
 */
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

        // === OUTER CONTAINER ===
        Panel outer = new Panel();
        outer.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        outer.setPreferredSize(new TerminalSize(70, 15)); // Optional: tweak for scaling
        outer.addComponent(new EmptySpace()); // Top padding

        // === INNER ROOT (CENTERED) ===
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        root.setPreferredSize(new TerminalSize(60, 15));

        // ---------- INFO COLUMN ----------
        Panel infoInner = new Panel(new LinearLayout(Direction.VERTICAL));
        infoInner.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        infoInner.addComponent(textBlock("Name", player.getName()));
        infoInner.addComponent(textBlock("Gold", String.valueOf(player.getGold())));
        infoInner.addComponent(textBlock("Completed", GameState.get().getMissionFlag() != null
                ? GameState.get().getMissionFlag().toString()
                : "—"));

        Panel infoPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        infoPanel.setPreferredSize(new TerminalSize(20, 15));
        infoPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        infoPanel.addComponent(new EmptySpace());
        infoPanel.addComponent(infoInner);
        infoPanel.addComponent(new EmptySpace());

        Component borderedInfo = withBorder("Info", infoPanel);

        // ---------- MENU COLUMN ----------
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
            // ⛑ Ensure spell and item IDs are reattached before saving
            GameState.get().getPlayer().rebuildSpellsFromIds();
            GameState.get().getPlayer().rebuildConsumablesFromIds();
            boolean success = GameState.get().saveToFile();
            MessageDialog.showMessageDialog(gui, "Save Game",
                    success ? "Game saved successfully!" : "Failed to save game.");
        }));

        menuInner.addComponent(new Button("Boost Strength to 200", () -> {
            // 🛠 DEV FEATURE — temporary boost for testing scaling/damage
            MessageDialogButton result = MessageDialog.showMessageDialog(
                    gui,
                    "Confirm Boost",
                    "Do you want to increase Strength to 200?",
                    MessageDialogButton.Yes,
                    MessageDialogButton.No
            );

            if (result == MessageDialogButton.Yes) {
                player.setStat(StatsType.STRENGTH, 200);
                MessageDialog.showMessageDialog(gui, "Boost Applied", "Strength increased to 200!");
            }
        }));

        menuInner.addComponent(new Button("Exit to Main Menu", () -> {
            window.close();
            SceneManager.get().switchTo(new MainMenu((MultiWindowTextGUI) gui));
        }));

        Panel menuButtons = new Panel(new LinearLayout(Direction.VERTICAL));
        menuButtons.setPreferredSize(new TerminalSize(40, 15));
        menuButtons.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        menuButtons.addComponent(new EmptySpace());
        menuButtons.addComponent(menuInner);
        menuButtons.addComponent(new EmptySpace());

        Component borderedMenu = withBorder("Menu", menuButtons);

        // === ADD TO ROOT & OUTER ===
        root.addComponent(borderedInfo);
        root.addComponent(borderedMenu);

        outer.addComponent(root);
        outer.addComponent(new EmptySpace()); // Bottom padding

        window.setComponent(outer);
        gui.addWindowAndWait(window);

        // 🪵 Dev log output
        DeveloperLogger.log("WorldHub entered, mission flag: "
                + (GameState.get().getMissionFlag() != null
                ? GameState.get().getMissionFlag()
                : "NONE"));
    }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
