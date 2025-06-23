package scenes.menu;

import characters.Player;
import com.googlecode.lanterna.gui2.*;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.worldhub.CharacterOverview;


import java.util.List;
import java.util.Map;

public class InventoryMenu implements Scene {
    private final WindowBasedTextGUI gui;
    private final Player player;
    private BasicWindow window;

    public InventoryMenu(WindowBasedTextGUI gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    @Override
    public void enter() {
        window = new BasicWindow("📦 Inventory");
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        if (player.getInventory().isEmpty()) {
            panel.addComponent(new Label("Your inventory is empty."));
        } else {
            for (Map.Entry<?, Integer> entry : player.getInventory().entrySet()) {
                panel.addComponent(new Label(entry.getKey().toString() + " x" + entry.getValue()));
            }
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("⬅ Back", () -> {
            window.close();
            SceneManager.get().switchTo(new CharacterOverview(gui, player));
        }));

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.CENTERED));
        gui.addWindowAndWait(window);
    }

    @Override
    public void handleInput() {}

    @Override
    public void exit() {
        if (window != null) gui.removeWindow(window);
    }
}
