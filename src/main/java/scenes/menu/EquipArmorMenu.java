package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Item;
import items.equip.Armor;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;

import java.util.Comparator;
import java.util.List;

/**
 * EquipArmorMenu is a UI screen that lets the player equip armor items
 * from their inventory. It uses vertical layout, paginated display, and a button-based selection system.
 * <p>
 * NOTE on Lanterna:
 * - Every UI interaction is modal and blocking unless you manage threading manually.
 * - Lanterna UI runs on a single UI thread — this makes logic easier but also riskier if blocking calls stack.
 * - Hints like CENTERED help, but the layout system is still fairly rigid.
 * - Window lifecycle (especially opening/closing/refreshing scenes) must be handled carefully
 * to avoid stacking windows or leaking references.
 */
public class EquipArmorMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    // Hard-coded pagination constant
    private static final int ITEMS_PER_PAGE = 2;

    public EquipArmorMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("Equip Armor");

        // === ROOT PANEL ===
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        // === CURRENTLY EQUIPPED ARMOR ===
        Armor currentArmor = player.getArmor();
        String equippedText = (currentArmor != null) ? currentArmor.getName() : "None";
        mainPanel.addComponent(new Label("Currently Equipped: " + equippedText));
        mainPanel.addComponent(new EmptySpace());

        // === FILTER INVENTORY TO ARMORS ===
        List<Armor> armorList = player.getInventory().keySet().stream()
                .filter(item -> item instanceof Armor)
                .map(item -> (Armor) item)
                .sorted(Comparator.comparing(Item::getName))
                .toList();

        // === HANDLE CASE: NO ARMORS ===
        if (armorList.isEmpty()) {
            mainPanel.addComponent(new Label("No armor available in inventory."));
        } else {
            int totalPages = (int) Math.ceil(armorList.size() / (double) ITEMS_PER_PAGE);
            final int[] currentPage = {0};  // workaround for lambda mutability

            Panel itemListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(itemListPanel);

            // === PAGE RENDER FUNCTION ===
            Runnable updatePage = () -> {
                itemListPanel.removeAllComponents();
                int start = currentPage[0] * ITEMS_PER_PAGE;
                int end = Math.min(start + ITEMS_PER_PAGE, armorList.size());

                for (int i = start; i < end; i++) {
                    Armor armor = armorList.get(i);
                    int quantity = player.getInventory().getOrDefault(armor, 0);

                    Panel itemBlock = new Panel(new LinearLayout(Direction.VERTICAL));
                    itemBlock.setPreferredSize(new TerminalSize(50, 4));
                    itemBlock.addComponent(new Label(armor.getName() + " x" + quantity));
                    itemBlock.addComponent(new Label(armor.getDescription()));
                    itemBlock.addComponent(new Label("Def: " + armor.getDefensePoints()));

                    // === EQUIP BUTTON ===
                    itemBlock.addComponent(new Button("Equip", () -> {
                        player.setArmor(armor);                    // Assign new armor
                        player.removeItemFromInventory(armor);     // Remove one copy
                        if (currentArmor != null) {
                            player.addItemToInventory(currentArmor); // Return old armor
                        }
                        MessageDialog.showMessageDialog(gui, "Equipped", "You equipped: " + armor.getName());
                        window.close();  // Close current menu to avoid stacking
                        SceneManager.get().switchTo(new EquipArmorMenu(gui, player));  // Refresh
                    }));

                    itemListPanel.addComponent(itemBlock);
                    itemListPanel.addComponent(new EmptySpace());
                }
            };

            // === PAGINATION CONTROLS ===
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
            paginationPanel.addComponent(new EmptySpace(new TerminalSize(1, 0)));
            paginationPanel.addComponent(next);

            updatePage.run(); // Initial render
            mainPanel.addComponent(new EmptySpace());
            mainPanel.addComponent(paginationPanel);
        }

        // === BACK BUTTON ===
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        // === FINAL CONFIG ===
        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED)); // Avoid layout bugs
        gui.addWindowAndWait(window); // Blocking call — careful with recursion or accidental infinite loops
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window); // Important: clean up to avoid visual ghosting or duplicated input capture
        }
    }
}
