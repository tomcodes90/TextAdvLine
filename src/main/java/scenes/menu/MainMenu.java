package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.missions.Tutorial;
import scenes.worldhub.WorldHub;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

/**
 * This is the main menu screen shown when the game starts.
 * It uses Lanterna components to render a vertically stacked menu in the center of the terminal.
 * <p>
 * Why we use these components:
 * - BasicWindow: A top-level container in Lanterna; we create and open this with the GUI thread.
 * - Panel: A flexible layout container, which can hold Labels, Buttons, and other Panels.
 * - MultiWindowTextGUI: Lanterna’s GUI manager responsible for rendering windows and managing input.
 * <p>
 * Important notes:
 * - You must use addWindowAndWait() to block and keep the window open until user exits/closes.
 * - Threading in Lanterna can be tricky. All UI changes must happen on the GUI thread,
 * so avoid doing game logic (e.g., loading) directly in the button callbacks if they are long-running.
 * - Windows should be explicitly closed or you risk memory leaks or overlapping GUI state.
 */
public class MainMenu implements Scene {

    private final BasicWindow window = new BasicWindow("Main Menu");  // Root window for the scene
    private final MultiWindowTextGUI gui;  // Lanterna’s GUI handler (injected from main entry point)

    public MainMenu(MultiWindowTextGUI gui) {
        this.gui = gui;

        // Main vertical container for the UI
        Panel outer = new Panel(new LinearLayout(Direction.VERTICAL));
        outer.setPreferredSize(new TerminalSize(40, 15));  // Fixed size to keep UI consistent
        outer.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        outer.addComponent(new EmptySpace(new TerminalSize(0, 3))); // Padding at the top

        // === Title Label ===
        Label title = new Label("SAUCE AND STEEL");
        title.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        title.setForegroundColor(TextColor.ANSI.YELLOW);
        outer.addComponent(title);
        outer.addComponent(new EmptySpace());

        // === Buttons ===
        Panel buttons = new Panel(new LinearLayout(Direction.VERTICAL));
        buttons.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        // --- Start Button ---
        buttons.addComponent(new Button("Start Game", () -> {
            DeveloperLogger.log("Start Game Clicked");

            // Transition to the tutorial scene
            SceneManager.get().switchTo(new Tutorial(gui));
            window.close(); // Important: close this window to avoid conflicts
        }));

        buttons.addComponent(new EmptySpace());

        // --- Load Button ---
        buttons.addComponent(new Button("Load Game", () -> {
            if (GameState.loadInstance()) {
                Player p = GameState.get().getPlayer();

                // We must rebuild transient data from saved JSON
                p.rebuildSpellsFromIds();
                p.rebuildConsumablesFromIds();

                // Proceed to world hub after loading
                SceneManager.get().switchTo(new WorldHub(gui, p));
                window.close();
            }
        }));

        buttons.addComponent(new EmptySpace());

        // --- Exit Button ---
        buttons.addComponent(new Button("Exit", () -> {
            DeveloperLogger.log("Exit Clicked");
            System.exit(0); // Immediate termination
        }));

        outer.addComponent(buttons);

        // Center the window on screen
        window.setHints(List.of(Window.Hint.CENTERED));

        // Attach the UI hierarchy to the window
        window.setComponent(outer);
    }

    @Override
    public void enter() {
        // This line blocks and keeps the UI open until the window is closed
        gui.addWindowAndWait(window);
    }

    @Override
    public void exit() {
        if (window.isVisible()) {
            window.close();
        }
    }

}
