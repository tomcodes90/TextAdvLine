package scenes.ui.battle;

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

public class ActionMenu {
    public static void showActionsMenu(TurnManager tm, Player player, Enemy enemy, Panel action, WindowBasedTextGUI gui) {
        EntityCard.refreshCards(player, enemy);
        action.removeAllComponents();

        SafeActionListBox menu = new SafeActionListBox(new TerminalSize(50, 4));

        menu.addItem("Attack", () -> tm.queuePlayerAction(new AttackAction(player, enemy)));
        menu.addItem("Cast Spell", () -> showSpellMenu(tm, player, enemy, action, gui));
        menu.addItem("Use Item", () -> showItemMenu(tm, player, enemy, action, gui));
        menu.addItem("Flee", () -> tm.queuePlayerAction(new FleeAction(player, tm)));


        action.addComponent(menu);
        gui.getGUIThread().invokeLater(menu::takeFocus);
        refreshSafe(gui);
    }

    private static void showSpellMenu(TurnManager tm, Player player, Enemy enemy, Panel action, WindowBasedTextGUI gui) {
        action.removeAllComponents();
        SafeActionListBox list = new SafeActionListBox(new TerminalSize(50, 7));

        Arrays.stream(player.getSpellsEquipped())
                .filter(Objects::nonNull)
                .forEach(spell -> {
                    boolean ready = spell.isReady();
                    String label = ready
                            ? spell.getName().name()
                            : spell.getName().name() + " (Cooldown: " + spell.getCooldownCounter() + ")";

                    Runnable itemAction = () -> {
                        if (!spell.isReady()) {
                            String msg = "\n         " + spell.getName().name() + " is still on cooldown!";
                            PlayerLogger.log(msg);
                            return;
                        }

                        // Defensive check: target must be alive
                        if (enemy == null || !enemy.isAlive()) {
                            PlayerLogger.log("⚠️ No valid target to cast the spell.");
                            return;
                        }

                        // Queue and close menu
                        tm.queuePlayerAction(new CastSpellAction(player, spell, enemy));
                    };

                    list.addItem(label, itemAction);
                });

        list.addItem("Back", () -> showActionsMenu(tm, player, enemy, action, gui));

        action.addComponent(list);
        gui.getGUIThread().invokeLater(list::takeFocus);
        refreshSafe(gui);
    }


    private static void showItemMenu(TurnManager tm, Player player, Enemy enemy, Panel action, WindowBasedTextGUI gui) {
        action.removeAllComponents();
        SafeActionListBox list = new SafeActionListBox(new TerminalSize(50, 7));

        Arrays.stream(player.getConsumablesEquipped())
                .filter(Objects::nonNull)
                .forEach(item ->
                        list.addItem(item.getName(),
                                () -> tm.queuePlayerAction(new UseItemAction(player, item))));

        list.addItem("Back", () -> showActionsMenu(tm, player, enemy, action, gui));
        action.addComponent(list);
        gui.getGUIThread().invokeLater(list::takeFocus);
        refreshSafe(gui);
    }

    static void refreshSafe(WindowBasedTextGUI gui) {
        try {
            gui.updateScreen();
        } catch (IOException ignored) { /* swallow */ }
    }
}
