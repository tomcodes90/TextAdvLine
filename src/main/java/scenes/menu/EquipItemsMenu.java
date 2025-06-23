package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Consumable;
import items.Item;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;

import java.util.List;

public class EquipItemsMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public EquipItemsMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🧪 Equip Consumables");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Select a consumable to equip:"));
        panel.addComponent(new EmptySpace());

        boolean hasConsumables = false;

        for (Item item : player.getInventory().keySet()) {
            if (item instanceof Consumable consumable) {
                hasConsumables = true;
                int quantity = player.getInventory().get(item);
                String label = consumable.getName() + " x" + quantity;
                panel.addComponent(new Button(label, () -> openSlotSelector(consumable)));
            }
        }

        if (!hasConsumables) {
            panel.addComponent(new Label("You have no consumables in your inventory."));
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(panel);
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
                // Prevent equipping duplicate items
                // Prevent equipping duplicate items in another slot
                for (int j = 0; j < player.getConsumablesEquipped().length; j++) {
                    if (j != slot && consumable.equals(player.getConsumablesEquipped()[j])) {
                        MessageDialog.showMessageDialog(gui, "Already Equipped", "This item is already equipped in another slot.");
                        return;
                    }
                }

// Prevent equipping in the same slot
                if (consumable.equals(player.getConsumablesEquipped()[slot])) {
                    MessageDialog.showMessageDialog(gui, "Already Equipped", "This item is already equipped in this slot.");
                    return;
                }


                // Ensure player owns at least one of this item
                int countInInventory = player.getInventory().getOrDefault(consumable, 0);
                if (countInInventory < 1) {
                    MessageDialog.showMessageDialog(gui, "Unavailable", "You don't have any more of this item.");
                    return;
                }

                // Swap logic: return old item to inventory if present
                Consumable previouslyEquipped = player.getConsumablesEquipped()[slot];
                if (previouslyEquipped != null) {
                    player.addItemToInventory(previouslyEquipped);
                }

                // Remove one from inventory and equip
                player.removeItemFromInventory(consumable);
                player.assignConsumableToSlot(consumable, slot);

                MessageDialog.showMessageDialog(gui, "Equipped", consumable.getName() + " equipped to slot " + (slot + 1));
                slotWindow.close();
                enter(); // Refresh menu
            }));
        }

        panel.addComponent(new Button("⬅ Cancel", slotWindow::close));
        slotWindow.setComponent(panel);
        slotWindow.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(slotWindow);
    }


    @Override
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
