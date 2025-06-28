package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;
import items.Item;
import util.UIHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * InventoryMenu shows the player's inventory using a paginated list.
 * Uses Lanterna's Panels and Buttons to build a scrollable interface.
 * <p>
 * ⚠️ Threading & rendering:
 * - All UI code here is safe because it's run inside Lanterna's GUI thread (via addWindowAndWait).
 * - If you ever fetch external data or delay, make sure to offload heavy work to another thread.
 * <p>
 * ⚙ Components:
 * - BasicWindow: The screen itself
 * - Panels: Stack content vertically, including item lists and pagination buttons
 * - Buttons: Navigate between pages or go back
 * <p>
 * Risks / Tips:
 * - Don’t forget to close your window before switching scene to avoid ghost windows.
 * - Always update components on the GUI thread, otherwise Lanterna may behave unexpectedly.
 */
public class InventoryMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public InventoryMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("Inventory");

        // Main layout container (vertical stack)
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Collect and sort inventory items alphabetically
        List<Item> items = player.getInventory().keySet().stream()
                .sorted(Comparator.comparing(Item::getName))
                .toList();

        // If inventory is empty, show message and skip pagination
        if (items.isEmpty()) {
            mainPanel.addComponent(new Label("Your inventory is empty."));
        } else {
            final int ITEMS_PER_PAGE = 6; // How many items to show per page
            final int totalPages = (int) Math.ceil(items.size() / (double) ITEMS_PER_PAGE);
            final int[] currentPage = {0}; // Mutable holder for current page

            Panel itemListWrapper = new Panel(new LinearLayout(Direction.VERTICAL));

            // Function to refresh visible items
            Runnable updatePage = () -> {
                itemListWrapper.removeAllComponents(); // clear old content
                itemListWrapper.addComponent(UIHelper.itemListPanel(currentPage[0], ITEMS_PER_PAGE));
            };

            // Initial render
            updatePage.run();
            mainPanel.addComponent(itemListWrapper);

            // Pagination controls
            Panel paginationPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
            Button prev = new Button("< Prev", () -> {
                if (currentPage[0] > 0) {
                    currentPage[0]--;
                    updatePage.run();
                }
            });
            Button next = new Button("Next >", () -> {
                if (currentPage[0] < totalPages - 1) {
                    currentPage[0]++;
                    updatePage.run();
                }
            });

            // Add pagination controls
            paginationPanel.addComponent(prev);
            paginationPanel.addComponent(new EmptySpace(new TerminalSize(1, 0))); // spacing
            paginationPanel.addComponent(next);

            mainPanel.addComponent(new EmptySpace());
            mainPanel.addComponent(paginationPanel);
        }

        // Back button: returns to character overview
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("⬅ Back", () -> {
            window.close(); // Always close current window before switching
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        // Attach the main layout to the window
        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED)); // center on screen

        // Show the window and block until closed
        gui.addWindowAndWait(window);
    }

    @Override
    public void exit() {
        // Remove window from GUI if it's still showing
        if (window != null) gui.removeWindow(window);
    }
}
