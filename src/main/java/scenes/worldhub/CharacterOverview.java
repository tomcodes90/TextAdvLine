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

        /* --------------------------------------------------
         * LEFT PANEL (Info + Menu)
         * -------------------------------------------------- */
        Panel leftPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        leftPanel.setPreferredSize(new TerminalSize(25, 23));
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

        /* --------------------------------------------------
         * RIGHT PANEL (split in two: Stats | Equipment)
         * -------------------------------------------------- */
        Panel rightPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        rightPanel.setPreferredSize(new TerminalSize(45, 23));
        rightPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        /* --- Stats Sub‑Panel --- */
        Panel statsPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        statsPanel.setPreferredSize(new TerminalSize(20, 21));
        statsPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        statsPanel.addComponent(textBlock("HP", player.getStat(characters.StatsType.HP) + "/" + player.getStat(characters.StatsType.MAX_HP)));
        statsPanel.addComponent(textBlock("STR", String.valueOf(player.getStat(characters.StatsType.STRENGTH))));
        statsPanel.addComponent(textBlock("INT", String.valueOf(player.getStat(characters.StatsType.INTELLIGENCE))));
        statsPanel.addComponent(textBlock("DEF", String.valueOf(player.getStat(characters.StatsType.DEFENSE))));
        statsPanel.addComponent(textBlock("SPD", String.valueOf(player.getStat(characters.StatsType.SPEED))));
        statsPanel.addComponent(textBlock("Weakness", player.getElementalWeakness().toString()));

        /* --- Equipment Sub‑Panel --- */
        Panel equipPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        equipPanel.setPreferredSize(new TerminalSize(25, 21));
        equipPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        equipPanel.addComponent(textBlock("Armor", (player.getArmor() != null ? player.getArmor().getName() : "None")));
        equipPanel.addComponent(textBlock("Weapon", (player.getWeapon() != null ? player.getWeapon().getName() : "None")));
        equipPanel.addComponent(new EmptySpace());

        equipPanel.addComponent(horizontalListBlock("Spells Equipped",
                Arrays.stream(player.getSpellsEquipped())
                        .filter(spell -> spell != null)
                        .map(Spell::getName)
                        .map(name -> name + " ")
                        .collect(Collectors.toList())));

        equipPanel.addComponent(horizontalListBlock("Consumables Equipped",
                Arrays.stream(player.getConsumablesEquipped())
                        .filter(consumable -> consumable != null)
                        .map(Consumable::getName)
                        .map(name -> " " + name)
                        .collect(Collectors.toList())));

        /* Add sub‑panels to right panel */
        rightPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        rightPanel.addComponent(statsPanel);
        rightPanel.addComponent(equipPanel);
        rightPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)), LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        /* --------------------------------------------------
         * Build the screen
         * -------------------------------------------------- */
        mainPanel.addComponent(leftPanel);
        mainPanel.addComponent(rightPanel);

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    @Override
    public void handleInput() {
        // No additional input handling required for this scene.
    }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
