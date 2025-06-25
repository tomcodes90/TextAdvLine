package scenes.menu;// File: scenes/character/EquipArmorMenu.java


import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.equip.Armor;
import items.Item;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;


import java.util.ArrayList;
import java.util.List;

public class EquipArmorMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public EquipArmorMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🛡 Equip Armor");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Show currently equipped armor
        Armor currentArmor = player.getArmor();
        String equippedText = (currentArmor != null) ? currentArmor.getName() : "None";
        panel.addComponent(new Label("Currently Equipped: " + equippedText));
        panel.addComponent(new EmptySpace());

        // Show list of armor in inventory
        List<Item> armorInInventory = new ArrayList<>();
        for (Item item : player.getInventory().keySet()) {
            if (item instanceof Armor) {
                armorInInventory.add(item);
            }
        }

        if (armorInInventory.isEmpty()) {
            panel.addComponent(new Label("No armor available in inventory."));
        } else {
            for (Item item : armorInInventory) {
                int quantity = player.getInventory().get(item);
                String label = item.getName() + " x" + quantity;
                panel.addComponent(new Button(label, () -> {
                    player.setArmor((Armor) item);
                    player.removeItemFromInventory(item);
                    if (currentArmor != null) {
                        player.addItemToInventory(currentArmor); // Swap old armor back into inventory
                    }
                    MessageDialog.showMessageDialog(gui, "Equipped", "You equipped: " + item.getName());
                    window.close();
                    SceneManager.get().switchTo(new EquipArmorMenu(gui, player)); // Refresh
                }));
            }
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

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
