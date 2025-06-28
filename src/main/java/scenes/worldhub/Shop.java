/**
 * 🏪 Shop: The item purchasing and selling screen.
 * Lets the player buy items unlocked by mission progress or sell items in their inventory.
 * <p>
 * 🧩 Features:
 * - Displays item list in pages (5 items per page).
 * - Dynamically filters available items depending on mission progression.
 * - Shows effects depending on item type (e.g., stat boosts, damage, defense).
 * - Handles gold updates and inventory adjustments for purchases/sales.
 * <p>
 * 📘 UI Notes:
 * - Menu built using Lanterna panels and buttons.
 * - Pagination supported via Prev/Next buttons.
 * - Includes confirmation dialog when selling.
 * <p>
 * 🧠 Dev Notes:
 * - Item unlocks are hardcoded in `isItemUnlocked()` based on mission flag.
 * - This shop is non-persistent: no vendor stock or quantity limits.
 * - BasicWindow is reused per section (buy/sell) with `gui.addWindowAndWait()`.
 */

package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import items.Item;
import items.consumables.StatEnhancer;
import items.equip.Armor;
import items.equip.Weapon;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.missions.MissionType;
import state.GameState;
import util.ItemRegistry;

import java.util.Comparator;
import java.util.List;

public class Shop implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;
    private static final int ITEMS_PER_PAGE = 5;

    public Shop(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("Item Shop");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Welcome to the Item Shop!"));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("Buy Items", this::openBuyMenu));
        panel.addComponent(new Button("Sell Items", this::openSellMenu));
        panel.addComponent(new Button("Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui, player));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    private void openBuyMenu() {
        showPaginatedItemMenu("Buy Items", true);
    }

    private void openSellMenu() {
        showPaginatedItemMenu("Sell Items", false);
    }

    /**
     * Renders a paginated list of items (either buying or selling mode).
     */
    private void showPaginatedItemMenu(String title, boolean isBuying) {
        BasicWindow menuWindow = new BasicWindow(title);
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Label goldLabel = new Label("Gold: " + player.getGold());
        mainPanel.addComponent(goldLabel);
        mainPanel.addComponent(new EmptySpace());

        List<Item> allItems = isBuying
                ? ItemRegistry.getAllItems().stream()
                .filter(item -> item.getPrice() > 0 && isItemUnlocked(item, GameState.get().getMissionFlag()))
                .sorted(Comparator.comparing(Item::getName))
                .toList()
                : player.getInventory().keySet().stream()
                .filter(item -> item.getPrice() > 0)
                .sorted(Comparator.comparing(Item::getName))
                .toList();

        int totalPages = (int) Math.ceil(allItems.size() / (double) ITEMS_PER_PAGE);
        final int[] currentPage = {0};

        Panel itemListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(itemListPanel);

        Runnable updatePage = () -> {
            itemListPanel.removeAllComponents();
            int start = currentPage[0] * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, allItems.size());

            for (int i = start; i < end; i++) {
                Item item = allItems.get(i);

                Panel itemPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                itemPanel.addComponent(new Label(item.getName() + (isBuying
                        ? " - " + item.getPrice() + "g"
                        : " x" + player.getInventory().getOrDefault(item, 0) + " - " + item.getPrice() + "g")));
                itemPanel.addComponent(new Label(item.getDescription()));
                itemPanel.addComponent(new Label(getEffectText(item)));

                Button actionButton = new Button(isBuying ? "Buy" : "Sell", () -> {
                    if (isBuying) {
                        if (player.getGold() >= item.getPrice()) {
                            player.decreaseGold(item.getPrice());
                            player.addItemToInventory(item);
                            MessageDialog.showMessageDialog(gui, "Purchase", "You bought: " + item.getName());
                            goldLabel.setText("Gold: " + player.getGold());
                        } else {
                            MessageDialog.showMessageDialog(gui, "Not enough gold", "You can't afford this item.");
                        }
                    } else {
                        MessageDialogButton res = MessageDialog.showMessageDialog(gui, "Confirm",
                                "Sell " + item.getName() + " for " + item.getPrice() + "g?",
                                MessageDialogButton.Yes, MessageDialogButton.No);
                        if (res == MessageDialogButton.Yes) {
                            player.removeItemFromInventory(item);
                            player.collectGold(item.getPrice());
                            goldLabel.setText("Gold: " + player.getGold());
                            menuWindow.close();
                            showPaginatedItemMenu(title, false);
                        }
                    }
                });

                itemPanel.addComponent(actionButton);
                itemPanel.addComponent(new EmptySpace());
                itemListPanel.addComponent(itemPanel);
            }
        };

        // === Pagination Buttons ===
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

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(paginationPanel);
        mainPanel.addComponent(new Button("Back", menuWindow::close));

        updatePage.run();
        menuWindow.setComponent(mainPanel);
        menuWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(menuWindow);
    }

    /**
     * Determines if the given item is available for purchase based on mission progress.
     */
    private boolean isItemUnlocked(Item item, MissionType currentMission) {
        String id = item.getId();
        return switch (id) {
            case "healing_potion", "sage_elixir", "power_elixir",
                 "iron_sword", "leather_armor" -> true;
            case "greater_healing_potion", "fortitude_tonic", "swift_draught" ->
                    currentMission != null && currentMission.ordinal() >= MissionType.MISSION_2.ordinal();
            case "elixir_of_life", "mind_elixir", "rage_brew" ->
                    currentMission != null && currentMission.ordinal() >= MissionType.MISSION_4.ordinal();
            case "steel_sword", "chainmail_armor" ->
                    currentMission != null && currentMission.ordinal() >= MissionType.MISSION_3.ordinal();
            case "crimson_blade", "plate_armor" ->
                    currentMission != null && currentMission.ordinal() >= MissionType.MISSION_5.ordinal();
            case "dragonfang_sword", "dragon_scale_armor" ->
                    currentMission != null && currentMission.ordinal() >= MissionType.MISSION_7.ordinal();
            default -> false;
        };
    }

    /**
     * Returns a short description of an item’s effect based on its type.
     */
    private String getEffectText(Item item) {
        if (item instanceof StatEnhancer s) {
            return "+" + s.getPointsToApply() + " for " + s.getLength() + " turns";
        } else if (item instanceof Weapon w) {
            return "Dmg: " + w.getDamage();
        } else if (item instanceof Armor a) {
            return "Def: " + a.getDefensePoints();
        }
        return " ";
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
