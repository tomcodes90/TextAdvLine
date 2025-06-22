package scenes;

import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class MainMenu {
    private final MultiWindowTextGUI gui;
    private final Runnable onStartBattle;

    public MainMenu(MultiWindowTextGUI gui, Runnable onStartBattle) {
        this.gui = gui;
        this.onStartBattle = onStartBattle;
    }

    public Window build() {
        BasicWindow win = new BasicWindow("Main Menu");
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Button startBtn = new Button("New Game", () -> {
            win.close(); // close the menu window first
            onStartBattle.run();
        });


        root.addComponent(startBtn);
        win.setComponent(root);
        win.setHints(List.of(Window.Hint.CENTERED));

        return win;
    }
}
