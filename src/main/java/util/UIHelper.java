package util;

import characters.Player;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import items.Item;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import state.GameState;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class UIHelper {
    public static void openSubmenu(Scene submenu, BasicWindow window) {
        window.close();
        SceneManager.get().switchTo(submenu);
    }

    public static Panel textBlock(String label, String value) {
        Panel block = new Panel(new LinearLayout(Direction.HORIZONTAL));
        block.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));

        Label key = new Label(label + ": ");
        key.setForegroundColor(TextColor.ANSI.BLUE);

        Label val = new Label(value);
        val.setForegroundColor(TextColor.ANSI.BLACK); // or TextColor.ANSI.DEFAULT for adaptive color

        block.addComponent(key);
        block.addComponent(val);

        return block;
    }

    public static Component withBorder(String title, Panel panel) {
        Border border = Borders.singleLine(title); // create empty border
        border.setComponent(panel);                // attach the panel inside
        return border;                             // return as Component

    }

    public static Panel itemListPanel(int page, int itemsPerPage) {
        Player player = GameState.get().getPlayer();
        List<Item> items = player.getInventory().keySet().stream()
                .sorted(Comparator.comparing(Item::getName))
                .toList();

        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());
        List<Item> pageItems = items.subList(start, end);

        Panel itemListPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        for (Item item : pageItems) {
            int quantity = player.getInventory().get(item);
            Panel itemPanel = new Panel(new LinearLayout(Direction.VERTICAL));
            itemPanel.setPreferredSize(new TerminalSize(50, 4));
            itemPanel.addComponent(new Label(item.getName() + " x" + quantity));
            itemPanel.addComponent(new Label(item.getDescription()));
            itemPanel.addComponent(new Label(getEffectText(item)));
            itemPanel.addComponent(new EmptySpace());
            itemListPanel.addComponent(itemPanel);
        }
        return itemListPanel;
    }


    public static Panel verticalListBlock(String title, List<String> items) {
        Panel block = new Panel(new LinearLayout(Direction.VERTICAL));
        block.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));

        Label titleLabel = new Label(title);
        titleLabel.setForegroundColor(TextColor.ANSI.BLUE);
        titleLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        block.addComponent(titleLabel);

        for (String item : items) {
            Label value = new Label("- " + item);
            value.setForegroundColor(TextColor.ANSI.BLACK); // black on light terminals, white on dark
            value.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Beginning));
            block.addComponent(value);
        }

        return block;
    }

    private static String getEffectText(Item item) {
        if (item instanceof items.consumables.StatEnhancer s) {
            return "+" + s.getPointsToApply() + " for " + s.getLength() + " turns";
        }
        if (item instanceof items.equip.Weapon w) {
            return "Dmg: " + w.getDamage();
        }
        if (item instanceof items.equip.Armor a) {
            return "Def:  " + a.getDefensePoints();
        }
        return "-";
    }

}