// File: scenes/ui/ActionMenu.java
package scenes.ui;

import battle.TurnManager;
import battle.actions.AttackAction;
import battle.actions.CastSpellAction;
import battle.actions.FleeAction;
import battle.actions.UseItemAction;
import characters.Enemy;
import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import util.PlayerLogger;
import util.SafeActionListBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * ActionMenu builds the player's in-battle command menu (Attack, Spell, Item, Flee).
 * <p>
 * 🧠 Highlights:
 * - Uses `SafeActionListBox` (custom wrapper to avoid Lanterna key bugs).
 * - Displays either the main action list or contextual submenus (spells, items).
 * - Ensures player can't use spells on cooldown or invalid targets.
 * <p>
 * ⚠️ UI Notes:
 * - The entire `Panel` (`action`) is cleared and rebuilt for each submenu.
 * - Always invokes `takeFocus()` on the list so keyboard navigation works smoothly.
 * - `gui.updateScreen()` is called manually due to Lanterna's async nature.
 */
public class ActionMenu {

    /**
     * Displays the main action list with 4 choices: Attack, Cast Spell, Use Item, Flee.
     */
    public static void showActionsMenu(TurnManager tm, Player player, Enemy enemy, Panel action, WindowBasedTextGUI gui) {
        // Update the player/enemy stat cards
        EntityCard.refreshCards(player, enemy);

        // Clear and build the main action panel
        action.removeAllComponents();

        SafeActionListBox menu = new SafeActionListBox(new TerminalSize(50, 4));

        menu.addItem("Attack", () -> tm.queuePlayerAction(new AttackAction(player, enemy)));
        menu.addItem("Cast Spell", () -> showSpellMenu(tm, player, enemy, action, gui));
        menu.addItem("Use Item", () -> showItemMenu(tm, player, enemy, action, gui));
        menu.addItem("Flee", () -> tm.queuePlayerAction(new FleeAction(tm)));

        action.addComponent(menu);

        // Force keyboard focus on menu for navigation
        gui.getGUIThread().invokeLater(menu::takeFocus);

        // Refresh the screen (needed to show changes)
        refreshSafe(gui);
    }

    /**
     * Displays the player's equipped spells and cooldown states.
     */
    private static void showSpellMenu(TurnManager tm, Player player, Enemy enemy, Panel action, WindowBasedTextGUI gui) {
        action.removeAllComponents();
        SafeActionListBox list = new SafeActionListBox(new TerminalSize(50, 7));

        Arrays.stream(player.getSpellsEquipped())
                .filter(Objects::nonNull)
                .forEach(spell -> {
                    boolean ready = spell.isReady();

                    // Append cooldown info if not ready
                    String label = ready
                            ? spell.getName().toString()
                            : spell.getName() + " (Cooldown: " + spell.getCooldownCounter() + ")";

                    Runnable itemAction = () -> {
                        if (!spell.isReady()) {
                            // Prevent casting if spell still cooling
                            PlayerLogger.log("\n         " + spell.getName().name() + " is still on cooldown!");
                            return;
                        }

                        // Ensure enemy is alive before casting
                        if (enemy == null || !enemy.isAlive()) {
                            PlayerLogger.log("⚠️ No valid target to cast the spell.");
                            return;
                        }

                        tm.queuePlayerAction(new CastSpellAction(player, spell, enemy));
                    };

                    list.addItem(label, itemAction);
                });

        list.addItem("Back", () -> showActionsMenu(tm, player, enemy, action, gui));
        action.addComponent(list);
        gui.getGUIThread().invokeLater(list::takeFocus);
        refreshSafe(gui);
    }

    /**
     * Displays the player's usable consumable items.
     */
    private static void showItemMenu(TurnManager tm, Player player, Enemy enemy, Panel action, WindowBasedTextGUI gui) {
        action.removeAllComponents();
        SafeActionListBox list = new SafeActionListBox(new TerminalSize(50, 7));

        Arrays.stream(player.getConsumablesEquipped())
                .filter(Objects::nonNull)
                .forEach(item -> list.addItem(
                        item.getName(),
                        () -> tm.queuePlayerAction(new UseItemAction(player, item)))
                );

        list.addItem("Back", () -> showActionsMenu(tm, player, enemy, action, gui));
        action.addComponent(list);
        gui.getGUIThread().invokeLater(list::takeFocus);
        refreshSafe(gui);
    }

    /**
     * Manually refreshes the GUI to reflect component updates.
     * ⚠️ Lanterna sometimes fails to repaint unless explicitly triggered.
     */
    static void refreshSafe(WindowBasedTextGUI gui) {
        try {
            gui.updateScreen();
        } catch (IOException ignored) {
            // Ignore safely; this prevents crash on minor repaint failures
        }
    }
}
