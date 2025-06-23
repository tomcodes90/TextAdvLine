// File: scenes/MainMenuScene.java
package scenes;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import java.util.List;

public class WorldHub implements Scene {
    private final WindowBasedTextGUI gui;
    private BasicWindow window;

    public WorldHub(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    @Override
    public void enter() {
        window = new BasicWindow("🌍 Main Menu");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("What would you like to do?"));
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("📖 Continue Story", () -> {
            window.close();
            SceneManager.get().switchTo(new Story(gui)); // placeholder
        }));


        panel.addComponent(new Button("🛒 Visit Shop", () -> {
            window.close();
            // placeholder
        }));

        panel.addComponent(new Button("🎒 Character Overview", () -> {
            window.close();
            // placeholder
        }));

        panel.addComponent(new Button("💾 Save Game", () -> {
            MessageDialog.showMessageDialog(gui, "Save", "Game saved! (not really yet)");
        }));

        panel.addComponent(new Button("🏁 Exit to Main Menu", () -> {
            window.close();
            // placeholder
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    @Override
    public void handleInput() {
        // No polling logic needed unless you implement non-blocking input
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
