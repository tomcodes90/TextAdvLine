package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Item;
import items.consumables.Consumable;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EquipItemsMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    private static final int ITEMS_PER_PAGE = 6;

    public EquipItemsMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🧪 Equip Consumables");
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(new Label("Select a consumable to equip:"));
        mainPanel.addComponent(new EmptySpace());

        List<Consumable> consumables = player.getInventory().keySet().stream()
                .filter(item -> item instanceof Consumable)
                .map(item -> (Consumable) item)
                .sorted(Comparator.comparing(Item::getName))
                .collect(Collectors.toList());

        if (consumables.isEmpty()) {
            mainPanel.addComponent(new Label("You have no consumables in your inventory."));
        } else {
            int totalPages = (int) Math.ceil(consumables.size() / (double) ITEMS_PER_PAGE);
            final int[] currentPage = {0};

            Panel itemListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            mainPanel.addComponent(itemListPanel);

            Runnable updatePage = () -> {
                itemListPanel.removeAllComponents();
                int start = currentPage[0] * ITEMS_PER_PAGE;
                int end = Math.min(start + ITEMS_PER_PAGE, consumables.size());

                for (int i = start; i < end; i++) {
                    Consumable consumable = consumables.get(i);
                    int quantity = player.getInventory().getOrDefault(consumable, 0);

                    Panel itemPanel = new Panel(new LinearLayout(Direction.VERTICAL));
                    itemPanel.setPreferredSize(new TerminalSize(50, 4));
                    itemPanel.addComponent(new Label(consumable.getName() + " x" + quantity));
                    itemPanel.addComponent(new Label(consumable.getDescription()));
                    itemPanel.addComponent(new Button("Equip", () -> openSlotSelector(consumable)));
                    itemListPanel.addComponent(itemPanel);
                    itemListPanel.addComponent(new EmptySpace());
                }
            };

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

            updatePage.run();
            mainPanel.addComponent(paginationPanel);
        }

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    private void openSlotSelector(Consumable consumable) {
        BasicWindow slotWindow = new BasicWindow("🎯 Choose Slot");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Choose a slot to equip:"));

        for (int i = 0; i < player.getConsumablesEquipped().length; i++) {
            int slot = i;
            String current = player.getConsumablesEquipped()[i] == null
                    ? "(empty)"
                    : player.getConsumablesEquipped()[i].getName();

            String label = String.format("Slot %d: %s", slot + 1, current);

            panel.addComponent(new Button(label, () -> {
                for (int j = 0; j < player.getConsumablesEquipped().length; j++) {
                    if (j != slot && consumable.equals(player.getConsumablesEquipped()[j])) {
                        MessageDialog.showMessageDialog(gui, "Already Equipped", "This item is already equipped in another slot.");
                        return;
                    }
                }

                if (consumable.equals(player.getConsumablesEquipped()[slot])) {
                    MessageDialog.showMessageDialog(gui, "Already Equipped", "This item is already in this slot.");
                    return;
                }

                int count = player.getInventory().getOrDefault(consumable, 0);
                if (count < 1) {
                    MessageDialog.showMessageDialog(gui, "Unavailable", "You don't have any more of this item.");
                    return;
                }

                Consumable old = player.getConsumablesEquipped()[slot];
                if (old != null) {
                    player.addItemToInventory(old);
                }

                player.removeItemFromInventory(consumable);
                player.assignConsumableToSlot(consumable, slot);

                MessageDialog.showMessageDialog(gui, "Equipped", consumable.getName() + " equipped to slot " + (slot + 1));
                slotWindow.close();
                window.close();
                SceneManager.get().switchTo(new EquipItemsMenu(gui, player));
            }));
        }

        panel.addComponent(new Button("⬅ Cancel", slotWindow::close));
        slotWindow.setComponent(panel);
        slotWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(slotWindow);
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
