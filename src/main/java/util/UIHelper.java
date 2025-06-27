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


}