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
        window = new BasicWindow("Story Mode");
        MissionType flag = GameState.get().getMissionFlag();

        Scene nextMission = switch (flag) {
            case TUTORIAL -> new Mission1((MultiWindowTextGUI) gui);
            case MISSION_1 -> new Mission2((MultiWindowTextGUI) gui);
            case MISSION_2 -> new Mission3((MultiWindowTextGUI) gui);
            case MISSION_3 -> new Mission4((MultiWindowTextGUI) gui);
            case MISSION_4 -> new Mission5((MultiWindowTextGUI) gui);
            case MISSION_5 -> new Mission6((MultiWindowTextGUI) gui);
            case MISSION_6 -> new Mission7((MultiWindowTextGUI) gui);
            case MISSION_7 -> new Mission8((MultiWindowTextGUI) gui);
            case MISSION_8 -> new WorldHub(gui, GameState.get().getPlayer());
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
