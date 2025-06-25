package scenes.worldhub;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import items.consumables.Consumable;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.*;
import spells.Spell;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.UIHelper.*;

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

        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // LEFT PANEL (Info + Menu)
        Panel leftPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        leftPanel.setPreferredSize(new TerminalSize(25, 30));
        leftPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        Panel leftContent = new Panel(new LinearLayout(Direction.VERTICAL));
        leftContent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        leftContent.addComponent(textBlock("Name", player.getName()));
        leftContent.addComponent(textBlock("Level", String.valueOf(player.getLevel())));
        leftContent.addComponent(textBlock("Gold", String.valueOf(player.getGold())));
        leftContent.addComponent(new EmptySpace());

        Panel buttonPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        buttonPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        buttonPanel.addComponent(new Button("📦 Inventory", () -> openSubmenu(new InventoryMenu(gui, player), window)));
        buttonPanel.addComponent(new Button("🛡 Equip Armor", () -> openSubmenu(new EquipArmorMenu(gui, player), window)));
        buttonPanel.addComponent(new Button("⚔ Equip Weapon", () -> openSubmenu(new EquipWeaponMenu(gui, player), window)));
        buttonPanel.addComponent(new Button("📘 Learn Spells", () -> openSubmenu(new LearnSpellsMenu(gui, player), window)));
        buttonPanel.addComponent(new Button("🧪 Equip Items", () -> openSubmenu(new EquipItemsMenu(gui, player), window)));
        buttonPanel.addComponent(new EmptySpace());
        buttonPanel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui, player));
        }));

        leftContent.addComponent(buttonPanel);

        leftPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        leftPanel.addComponent(leftContent);
        leftPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        // RIGHT PANEL (Stats + Equipment + Spells + Consumables)
        Panel rightPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        rightPanel.setPreferredSize(new TerminalSize(55, 40));
        rightPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        Panel rightContent = new Panel(new LinearLayout(Direction.VERTICAL));
        rightContent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        rightContent.addComponent(textBlock("HP", player.getStat(characters.StatsType.HP) + "/" + player.getStat(characters.StatsType.MAX_HP)));
        rightContent.addComponent(textBlock("STR", String.valueOf(player.getStat(characters.StatsType.STRENGTH))));
        rightContent.addComponent(textBlock("INT", String.valueOf(player.getStat(characters.StatsType.INTELLIGENCE))));
        rightContent.addComponent(textBlock("DEF", String.valueOf(player.getStat(characters.StatsType.DEFENSE))));
        rightContent.addComponent(textBlock("SPD", String.valueOf(player.getStat(characters.StatsType.SPEED))));
        rightContent.addComponent(textBlock("Weakness", player.getElementalWeakness().toString()));
        rightContent.addComponent(new EmptySpace());

        rightContent.addComponent(textBlock("Armor", (player.getArmor() != null ? player.getArmor().getName() : "None")));
        rightContent.addComponent(textBlock("Weapon", (player.getWeapon() != null ? player.getWeapon().getName() : "None")));
        rightContent.addComponent(new EmptySpace());


        rightContent.addComponent(horizontalListBlock("Spells Equipped",
                Arrays.stream(player.getSpellsEquipped())
                        .filter(spell -> spell != null)
                        .map(spell -> "- " + spell.getName())
                        .collect(Collectors.toList())));

        rightContent.addComponent(horizontalListBlock("Consumables Equipped",
                Arrays.stream(player.getConsumablesEquipped())
                        .filter(consumable -> consumable != null)
                        .map(consumable -> "- " + consumable.getName())
                        .collect(Collectors.toList())));

        rightPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        rightPanel.addComponent(rightContent);
        rightPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        // Add both panels to main
        mainPanel.addComponent(leftPanel);
        mainPanel.addComponent(rightPanel);

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}