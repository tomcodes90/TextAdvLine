// File: scenes/Story.java
package scenes;

import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class Story implements Scene {
    private final WindowBasedTextGUI gui;
    private BasicWindow window;

    public Story(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    @Override
    public void enter() {
        window = new BasicWindow("📖 Story Mode");

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("The story continues..."));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Back", () -> {
            window.close();
            SceneManager.get().switchTo(new WorldHub(gui));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindow(window);
    }

    @Override
    public void handleInput() {
        // Can be extended for custom input
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
