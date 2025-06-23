package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.*;

import java.util.List;

public class CharacterOverview implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public CharacterOverview(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🎒 Character Overview");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Name: " + player.getName()));
        panel.addComponent(new Label("Level: " + player.getLevel()));
        panel.addComponent(new Label("Gold: " + player.getGold()));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("📦 Inventory", () -> openSubmenu(new InventoryMenu(gui, player))));
        panel.addComponent(new Button("🛡 Equip Armor", () -> openSubmenu(new EquipArmorMenu(gui, player))));
        panel.addComponent(new Button("⚔ Equip Weapon", () -> openSubmenu(new EquipWeaponMenu(gui, player))));
        panel.addComponent(new Button("📘 Learn Spells", () -> openSubmenu(new LearnSpellsMenu(gui, player))));
        panel.addComponent(new Button("🧪 Equip Items", () -> openSubmenu(new EquipItemsMenu(gui, player))));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui, player));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    private void openSubmenu(Scene submenu) {
        window.close();
        SceneManager.get().switchTo(submenu);
    }

    @Override
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
