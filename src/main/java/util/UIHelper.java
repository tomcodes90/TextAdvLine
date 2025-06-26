package util;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;

import java.util.List;
import java.util.Objects;

public class UIHelper {
    public static void openSubmenu(Scene submenu, BasicWindow window) {
        window.close();
        SceneManager.get().switchTo(submenu);
    }

    public static Label centeredLabel(String text) {
        Label label = new Label(text);
        label.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
        label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        return label;
    }


    public static Label valueLabel(String text) {
        Label label = new Label(text);
        label.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        return label;
    }

    public static Panel textBlock(String label, String value) {
        Panel block = new Panel(new LinearLayout(Direction.VERTICAL));
        block.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        block.addComponent(centeredLabel(label));
        block.addComponent(valueLabel(value));
        return block;
    }

    public static Component withBorder(String title, Panel panel) {
        Border border = Borders.singleLine(title); // create empty border
        border.setComponent(panel);                // attach the panel inside
        return border;                             // return as Component

    }


    public static Panel verticalListBlock(String title, List<String> items) {
        Panel block = new Panel(new LinearLayout(Direction.VERTICAL));
        block.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        block.addComponent(centeredLabel(title));

        for (String item : items) {
            block.addComponent(valueLabel(item));
        }

        return block;
    }

}