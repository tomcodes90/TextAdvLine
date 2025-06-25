package util;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;


public class UIHelper {

    public static Label centeredLabel(String text) {
        return new Label(text).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
    }

    public static Panel textBlock(String label, String value) {
        Panel block = new Panel(new LinearLayout(Direction.VERTICAL));
        block.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        labelComponent.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);


        Label valueComponent = new Label(value);
        valueComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        block.addComponent(centeredLabel(label));
        block.addComponent(valueComponent);

        return block;
    }

    public static void openSubmenu(Scene submenu, BasicWindow window) {
        window.close();
        SceneManager.get().switchTo(submenu);
    }

    public static Panel horizontalListBlock(String title, java.util.List<String> items) {
        Panel outer = new Panel(new LinearLayout(Direction.VERTICAL));

        outer.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        outer.addComponent(centeredLabel(title));

        Panel row = new Panel(new LinearLayout(Direction.HORIZONTAL));
        for (String item : items) {
            Label label = new Label(item);
            label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            row.addComponent(label);
            row.addComponent(new EmptySpace()); // Optional spacing
        }

        outer.addComponent(row);
        return outer;
    }
}