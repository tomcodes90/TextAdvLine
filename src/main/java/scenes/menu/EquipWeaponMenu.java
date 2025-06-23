package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import items.Item;
import items.Weapon;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;

import java.util.ArrayList;
import java.util.List;

public class EquipWeaponMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public EquipWeaponMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🗡 Equip Weapon");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        // Currently equipped weapon
        Weapon currentWeapon = player.getWeapon();
        String equippedText = (currentWeapon != null) ? currentWeapon.getName() : "None";
        panel.addComponent(new Label("Currently Equipped: " + equippedText));
        panel.addComponent(new EmptySpace());

        // Filter weapons from inventory
        List<Item> weaponsInInventory = new ArrayList<>();
        for (Item item : player.getInventory().keySet()) {
            if (item instanceof Weapon) {
                weaponsInInventory.add(item);
            }
        }

        if (weaponsInInventory.isEmpty()) {
            panel.addComponent(new Label("No weapons available in inventory."));
        } else {
            for (Item item : weaponsInInventory) {
                int quantity = player.getInventory().get(item);
                String label = item.getName() + " x" + quantity;
                panel.addComponent(new Button(label, () -> {
                    player.setWeapon((Weapon) item);
                    player.removeItemFromInventory(item);
                    if (currentWeapon != null) {
                        player.addItemToInventory(currentWeapon); // Put old weapon back
                    }
                    MessageDialog.showMessageDialog(gui, "Equipped", "You equipped: " + item.getName());
                    window.close();
                    SceneManager.get().switchTo(new EquipWeaponMenu(gui, player)); // Refresh
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
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
