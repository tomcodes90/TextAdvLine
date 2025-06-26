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
import java.util.Objects;
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
        /* ------------ root window ------------ */
        window = new BasicWindow("Character Overview");

        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        /* ========== LEFT COLUMN (Info + Menu) ========== */
        Panel infoPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        infoPanel.setPreferredSize(new TerminalSize(25, 23));
        infoPanel.addComponent(textBlock("Name", player.getName()));
        infoPanel.addComponent(textBlock("Level", String.valueOf(player.getLevel())));
        infoPanel.addComponent(textBlock("Gold", String.valueOf(player.getGold())));

        Panel menuPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        menuPanel.setPreferredSize(new TerminalSize(25, 23));
        menuPanel.addComponent(new Button("Inventory", () -> openSubmenu(new InventoryMenu(gui, player), window)));
        menuPanel.addComponent(new Button("Equip Armor", () -> openSubmenu(new EquipArmorMenu(gui, player), window)));
        menuPanel.addComponent(new Button("Equip Weapon", () -> openSubmenu(new EquipWeaponMenu(gui, player), window)));
        menuPanel.addComponent(new Button("Learn Spells", () -> openSubmenu(new LearnSpellsMenu(gui, player), window)));
        menuPanel.addComponent(new Button("Equip Items", () -> openSubmenu(new EquipItemsMenu(gui, player), window)));
        menuPanel.addComponent(new EmptySpace());
        menuPanel.addComponent(new Button("Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui, player));
        }));

        Panel leftColumn = new Panel(new LinearLayout(Direction.VERTICAL));
        leftColumn.setPreferredSize(new TerminalSize(25, 23));
        leftColumn.addComponent(withBorder("Info", infoPanel));
        leftColumn.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        leftColumn.addComponent(withBorder("Menu", menuPanel));

        /* ========== RIGHT COLUMN (Stats + Equipment) ========== */
        Panel statsPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        statsPanel.setPreferredSize(new TerminalSize(25, 23));
        statsPanel.addComponent(textBlock("HP", player.getStat(characters.StatsType.HP) + "/" +
                player.getStat(characters.StatsType.MAX_HP)));
        statsPanel.addComponent(textBlock("STR", String.valueOf(player.getStat(characters.StatsType.STRENGTH))));
        statsPanel.addComponent(textBlock("INT", String.valueOf(player.getStat(characters.StatsType.INTELLIGENCE))));
        statsPanel.addComponent(textBlock("DEF", String.valueOf(player.getStat(characters.StatsType.DEFENSE))));
        statsPanel.addComponent(textBlock("SPD", String.valueOf(player.getStat(characters.StatsType.SPEED))));
        statsPanel.addComponent(textBlock("Weakness", player.getElementalWeakness().toString()));

        Panel equipPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        equipPanel.setPreferredSize(new TerminalSize(25, 23));
        equipPanel.addComponent(textBlock("Armor",
                player.getArmor() != null ? player.getArmor().getName() : "None"));
        equipPanel.addComponent(textBlock("Weapon",
                player.getWeapon() != null ? player.getWeapon().getName() : "None"));
        equipPanel.addComponent(verticalListBlock("Spells Equipped",
                Arrays.stream(player.getSpellsEquipped())
                        .filter(Objects::nonNull)
                        .map(spell -> spell.getName().toString())
                        .collect(Collectors.toList())));
        equipPanel.addComponent(verticalListBlock("Consumables Equipped",
                Arrays.stream(player.getConsumablesEquipped())
                        .filter(Objects::nonNull)
                        .map(Consumable::getName)
                        .collect(Collectors.toList())));

        Panel rightColumn = new Panel(new LinearLayout(Direction.VERTICAL));
        rightColumn.setPreferredSize(new TerminalSize(45, 25));
        rightColumn.addComponent(withBorder("Stats", statsPanel));
        rightColumn.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        rightColumn.addComponent(withBorder("Equipment", equipPanel));

        /* ------------ assemble & show ------------ */
        mainPanel.addComponent(leftColumn);
        mainPanel.addComponent(rightColumn);

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    @Override
    public void handleInput() { /* none */ }

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
