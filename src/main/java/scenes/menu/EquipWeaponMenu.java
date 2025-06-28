package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Item;
import items.equip.Weapon;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EquipWeaponMenu allows the player to choose a weapon from their inventory
 * and equip it. Weapons are shown in a paginated layout using Lanterna.
 * <p>
 * 💡 Lanterna Considerations:
 * - All rendering is done in a GUI thread (safe via addWindowAndWait)
 * - We use panels to structure vertical stacking and add spacing
 * - We must manage pagination ourselves (Lanterna doesn’t do it for us)
 * - Refreshing the list is done via `Runnable updatePage` for reusability
 */
public class EquipWeaponMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    private static final int ITEMS_PER_PAGE = 2;  // How many weapons to show per page

    public EquipWeaponMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        // Create a new window for the weapon equip screen
        window = new BasicWindow("Equip Weapon");

        // Main vertical layout panel
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Show currently equipped weapon
        Weapon currentWeapon = player.getWeapon();
        String equippedText = (currentWeapon != null) ? currentWeapon.getName() : "None";
        mainPanel.addComponent(new Label("Currently Equipped: " + equippedText));
        mainPanel.addComponent(new EmptySpace());

        // Fetch all weapons from inventory and sort them by name
        List<Weapon> weapons = player.getInventory().keySet().stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .sorted(Comparator.comparing(Item::getName))
                .collect(Collectors.toList());

        // If no weapons, display fallback message
        if (weapons.isEmpty()) {
            mainPanel.addComponent(new Label("No weapons available in inventory."));
        } else {
            // Calculate pages needed
            int totalPages = (int) Math.ceil(weapons.size() / (double) ITEMS_PER_PAGE);
            final int[] currentPage = {0}; // must be mutable for lambda

            // Panel that will be refreshed to show current page
            Panel itemListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(itemListPanel);

            // Runnable to refresh page content
            Runnable updatePage = () -> {
                itemListPanel.removeAllComponents(); // Clear previous items

                int start = currentPage[0] * ITEMS_PER_PAGE;
                int end = Math.min(start + ITEMS_PER_PAGE, weapons.size());

                for (int i = start; i < end; i++) {
                    Weapon weapon = weapons.get(i);
                    int quantity = player.getInventory().getOrDefault(weapon, 0);

                    // Build vertical block for each weapon
                    Panel itemBlock = new Panel(new LinearLayout(Direction.VERTICAL));
                    itemBlock.setPreferredSize(new TerminalSize(50, 4));
                    itemBlock.addComponent(new Label(weapon.getName() + " x" + quantity));
                    itemBlock.addComponent(new Label(weapon.getDescription()));
                    itemBlock.addComponent(new Label("Dmg: " + weapon.getDamage()));

                    // Equip button logic
                    itemBlock.addComponent(new Button("Equip", () -> {
                        // Equip the selected weapon
                        player.setWeapon(weapon);

                        // Remove newly equipped weapon from inventory
                        player.removeItemFromInventory(weapon);

                        // If something was previously equipped, return it to inventory
                        if (currentWeapon != null) {
                            player.addItemToInventory(currentWeapon);
                        }

                        // Show confirmation and reopen refreshed view
                        MessageDialog.showMessageDialog(gui, "Equipped", "You equipped: " + weapon.getName());
                        window.close();
                        SceneManager.get().switchTo(new EquipWeaponMenu(gui, player));
                    }));

                    itemListPanel.addComponent(itemBlock);
                    itemListPanel.addComponent(new EmptySpace()); // Space between items
                }
            };

            // Pagination panel with Prev/Next buttons
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
            paginationPanel.addComponent(prev);
            paginationPanel.addComponent(new EmptySpace(new TerminalSize(1, 0))); // spacing
            paginationPanel.addComponent(next);

            // Load initial page
            updatePage.run();

            // Add pagination to main panel
            mainPanel.addComponent(new EmptySpace());
            mainPanel.addComponent(paginationPanel);
        }

        // Back button to return to Character Overview
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        // Final window setup and blocking UI
        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED)); // center on screen
        gui.addWindowAndWait(window); // block until closed
    }


    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window); // clean up
    }
}
