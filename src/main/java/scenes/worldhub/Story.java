// File: scenes/Story.java
package scenes.worldhub;

import com.googlecode.lanterna.gui2.*;
import scenes.missions.MissionType;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.missions.*;
import state.GameState;
import util.DeveloperLogger;

import java.util.List;

public class Story implements Scene {
    private final WindowBasedTextGUI gui;
    private BasicWindow window;

    public Story(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    @Override
    public void enter() {
        DeveloperLogger.log("Mission flag at story entry: " + GameState.get().getMissionFlag());
        window = new BasicWindow("📖 Story Mode");
        MissionType flag = GameState.get().getMissionFlag();

        Scene nextMission = switch (flag) {
            case null -> new Mission1(gui, GameState.get().getPlayer());
            case MISSION_1, MISSION_2, MISSION_3, MISSION_4, MISSION_5, MISSION_6, MISSION_7 -> null;
            case MISSION_8 -> null; // No more missions
        };

        if (nextMission != null) {
            SceneManager.get().switchTo(nextMission);
        } else {
            Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
            panel.addComponent(new Label("You’ve completed all available missions!"));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Button("Back", () -> {
                window.close();
                SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer()));
            }));

            window.setComponent(panel);
            window.setHints(List.of(Window.Hint.CENTERED));
            gui.addWindow(window);
        }
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
