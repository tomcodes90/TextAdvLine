// File: scenes/worldhub/CharacterOverview.java
package scenes.worldhub;

import characters.Player;
import characters.StatsType;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import items.consumables.Consumable;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.menu.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static util.UIHelper.*;

/**
 * 🧍 CharacterOverview: Displays a full summary of the player's status.
 * This screen is accessible from the WorldHub and acts as a character profile hub.
 * <p>
 * 🧩 Layout Overview:
 * - Horizontal root with two vertical columns:
 * 🔹 Left Column: Info (name, gold, exp), Stats (HP, STR, etc.)
 * 🔹 Right Column: Menu (equip, inventory, etc.), Equipment (gear + spells/items equipped)
 * <p>
 * 📘 UI Notes:
 * - Uses `centreBox()` and `withBorder()` helpers to wrap sections consistently.
 * - Stats are dynamically pulled from the Player instance.
 * - Menu buttons open respective submenus using the common openSubmenu pattern.
 * <p>
 * 🧠 Dev Notes:
 * - This screen is read-only; all interactions route to dedicated submenus.
 * - Uses `EmptySpace()` liberally for spacing and alignment.
 */

public class CharacterOverview implements Scene {

    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public CharacterOverview(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    /* ───────────────────────────── helper ───────────────────────────── */
    private static Panel centreBox(Component inner, int w, int h) {
        Panel v = new Panel(new LinearLayout(Direction.VERTICAL));
        v.setPreferredSize(new TerminalSize(w, h));
        v.addComponent(new EmptySpace());
        inner.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        v.addComponent(inner);
        v.addComponent(new EmptySpace());

        Panel hWrap = new Panel(new LinearLayout(Direction.HORIZONTAL));
        hWrap.setPreferredSize(new TerminalSize(w, h));
        v.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        hWrap.addComponent(v);
        return hWrap;
    }

    /* ───────────────────────────── enter ────────────────────────────── */
    @Override
    public void enter() {

        window = new BasicWindow("Character Overview");
        window.setHints(List.of(Window.Hint.CENTERED));

        /* ─ Outer vertical wrapper ─ */
        Panel outer = new Panel(new LinearLayout(Direction.VERTICAL));
        outer.setPreferredSize(new TerminalSize(55, 30));
        outer.addComponent(new EmptySpace());

        /* ─ Root H-panel ─ */
        Panel root = new Panel(new LinearLayout(Direction.HORIZONTAL));
        root.setPreferredSize(new TerminalSize(50, 35));
        root.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        /* -------------------- common boxes -------------------- */
        // Info
        Panel infoInner = new Panel(new LinearLayout(Direction.VERTICAL));
        infoInner.addComponent(textBlock("Name", player.getName()));
        infoInner.addComponent(textBlock("Level", String.valueOf(player.getLevel())));
        infoInner.addComponent(textBlock("Gold", String.valueOf(player.getGold())));
        infoInner.addComponent(textBlock("EXP", String.valueOf(player.getExp())));
        infoInner.addComponent(textBlock("Next Level", String.valueOf(player.getExpToLevelUp())));
        Component infoBox = withBorder("Info", centreBox(infoInner, 25, 9));

        // Menu
        Panel menuInner = new Panel(new LinearLayout(Direction.VERTICAL));
        menuInner.addComponent(new Button("Inventory",
                () -> openSubmenu(new InventoryMenu(gui, player), window)));
        menuInner.addComponent(new Button("Equip Armor",
                () -> openSubmenu(new EquipArmorMenu(gui, player), window)));
        menuInner.addComponent(new Button("Equip Weapon",
                () -> openSubmenu(new EquipWeaponMenu(gui, player), window)));
        menuInner.addComponent(new Button("Learn Spells",
                () -> openSubmenu(new EquipSpellsMenu(gui, player), window)));
        menuInner.addComponent(new Button("Equip Items",
                () -> openSubmenu(new EquipItemsMenu(gui, player), window)));
        menuInner.addComponent(new EmptySpace());
        menuInner.addComponent(new Button("Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui, player));
        }));
        Component menuBox = withBorder("Menu", centreBox(menuInner, 25, 9));

        // Stats
        Panel statsInner = new Panel(new LinearLayout(Direction.VERTICAL));
        statsInner.addComponent(textBlock("HP",
                player.getStat(StatsType.HP) + "/" + player.getStat(StatsType.MAX_HP)));
        statsInner.addComponent(textBlock("STR", String.valueOf(player.getStat(StatsType.STRENGTH))));
        statsInner.addComponent(textBlock("INT", String.valueOf(player.getStat(StatsType.INTELLIGENCE))));
        statsInner.addComponent(textBlock("DEF", String.valueOf(player.getStat(StatsType.DEFENSE))));
        statsInner.addComponent(textBlock("SPD", String.valueOf(player.getStat(StatsType.SPEED))));
        statsInner.addComponent(textBlock("Weakness", player.getElementalWeakness().toString()));
        Component statsBox = withBorder("Stats", centreBox(statsInner, 25, 15));

        // Equipment
        Panel equipInner = new Panel(new LinearLayout(Direction.VERTICAL));
        // Armor
        Panel armorBlock = new Panel(new LinearLayout(Direction.VERTICAL));
        Label armorLabel = new Label("Armor");
        armorLabel.setForegroundColor(TextColor.ANSI.BLUE);
        armorLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));
        armorBlock.addComponent(armorLabel);

        Label armorValue = new Label(player.getArmor() != null ? player.getArmor().getName() : "None");
        armorValue.setForegroundColor(TextColor.ANSI.BLACK);
        armorValue.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));
        armorBlock.addComponent(armorValue);
        equipInner.addComponent(armorBlock);

        Panel weaponBlock = new Panel(new LinearLayout(Direction.VERTICAL));
        Label weaponLabel = new Label("Weapon");
        weaponLabel.setForegroundColor(TextColor.ANSI.BLUE);
        weaponLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));
        weaponBlock.addComponent(weaponLabel);

        Label weaponValue = new Label(player.getWeapon() != null ? player.getWeapon().getName() : "None");
        weaponValue.setForegroundColor(TextColor.ANSI.BLACK);
        weaponValue.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));
        weaponBlock.addComponent(weaponValue);

        equipInner.addComponent(weaponBlock);
        equipInner.addComponent(verticalListBlock(" Spells Equipped",
                Arrays.stream(player.getSpellsEquipped())
                        .filter(Objects::nonNull)
                        .map(sp -> sp.getName().toString())
                        .collect(Collectors.toList())));
        equipInner.addComponent(verticalListBlock("Items Equipped",
                Arrays.stream(player.getConsumablesEquipped())
                        .filter(Objects::nonNull)
                        .map(Consumable::getName)
                        .collect(Collectors.toList())));
        Component equipBox = withBorder("Equipment", centreBox(equipInner, 25, 15));

        /* ========== LEFT COLUMN (Equipment + Menu) ========== */
        Panel leftCol = new Panel(new LinearLayout(Direction.VERTICAL));
        leftCol.setPreferredSize(new TerminalSize(25, 40));
        Panel rightCol = new Panel(new LinearLayout(Direction.VERTICAL));
        rightCol.setPreferredSize(new TerminalSize(45, 40));

        rightCol.addComponent(menuBox);        // <-- swapped here
        rightCol.addComponent(new EmptySpace());
        rightCol.addComponent(equipBox);
        leftCol.addComponent(infoBox);       // now on top
        leftCol.addComponent(new EmptySpace());
        leftCol.addComponent(statsBox);        // now on bottom

        /* assemble */
        root.addComponent(leftCol);
        root.addComponent(rightCol);

        outer.addComponent(root);
        outer.addComponent(new EmptySpace());

        window.setComponent(outer);
        gui.addWindowAndWait(window);
    }

    /* ───────────────────────────── boilerplate ─────────────────────── */

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
/*
  🧩 Layout Overview:
  ┌────────────────────────────┬─────────────────────────────┐
  │        INFO BOX            │          MENU BOX           │
  │  - Name, Level, Gold       │  - Inventory                │
  │  - EXP, Next Level         │  - Equip Armor              │
  ├────────────────────────────┤  - Equip Weapon             │
  │        STATS BOX           │  - Learn Spells             │
  │  - HP, STR, INT, DEF, SPD  │  - Equip Items              │
  │  - Weakness                │                             │
  └────────────────────────────┼─────────────────────────────┘
  │        EQUIPMENT BOX        │
  │  - Armor / Weapon           │
  │  - Spells Equipped          │
  │  - Items Equipped           │
  └─────────────────────────────┘
 */