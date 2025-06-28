/**
 * 📖 Story: Handles mission progression.
 * Acts as a dispatcher that decides which mission scene to load based on player progress.
 * <p>
 * 🧩 Behavior:
 * - Reads the mission flag from GameState.
 * - Depending on the flag, it routes to the appropriate MissionX scene.
 * - If all missions are completed, it triggers the final dialogue sequence.
 * <p>
 * 🧠 Notes:
 * - Uses Java 17 enhanced `switch` syntax with `yield`.
 * - DialogueService is used to run the final game-ending dialogues.
 * - If the mission flag is invalid (null or unknown), shows an error popup.
 * - No input is handled manually in this scene — it switches automatically.
 */

package scenes.worldhub;

import com.googlecode.lanterna.gui2.*;
import dialogues.Dialogue;
import dialogues.DialogueService;
import scenes.manager.Scene;
import scenes.manager.SceneManager;
import scenes.missions.*;
import scenes.missions.MissionType;
import scenes.ui.DialogueUI;
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
        window = new BasicWindow("Story Mode");

        // === Get current mission progress from GameState ===
        MissionType flag = GameState.get().getMissionFlag();
        DeveloperLogger.log("Mission flag at story entry: " + flag);

        // === Determine which scene to load based on mission progress ===
        Scene nextMission = switch (flag) {
            case TUTORIAL -> new Mission1((MultiWindowTextGUI) gui);
            case MISSION_1 -> new Mission2((MultiWindowTextGUI) gui);
            case MISSION_2 -> new Mission3((MultiWindowTextGUI) gui);
            case MISSION_3 -> new Mission4((MultiWindowTextGUI) gui);
            case MISSION_4 -> new Mission5((MultiWindowTextGUI) gui);
            case MISSION_5 -> new Mission6((MultiWindowTextGUI) gui);
            case MISSION_6 -> new Mission7((MultiWindowTextGUI) gui);
            case MISSION_7 -> new Mission8((MultiWindowTextGUI) gui);

            case MISSION_8 -> {
                // === End of game: show final dialogue sequence ===
                DialogueService.getInstance().setUI(new DialogueUI(gui));
                DialogueService.getInstance().runDialogues(List.of(
                        new Dialogue("Narrator", "You've stirred, simmered, sautéed, and saved the culinary world."),
                        new Dialogue("Nonna", "The sauce is complete, the table is set... and you're still here?"),
                        new Dialogue("Hero", "I thought maybe there was dessert..."),
                        new Dialogue("Nonna", "The only thing left is a nap. Go touch some grass, bambino."),
                        new Dialogue("Narrator", "Thank you for playing. May your pasta always be al dente, and your enemies always under-seasoned.")
                ), () -> SceneManager.get().switchTo(new WorldHub(gui, GameState.get().getPlayer())));
                yield null;
            }

            // default -> {
            // === Defensive fallback if flag is corrupted or unknown ===
            //  MessageDialog.showMessageDialog(gui, "Error", "Unknown mission state: " + flag);
            // yield null;
            //}  Default line not needed.
        };

        // === If a next mission exists, switch to it ===
        if (nextMission != null) {
            SceneManager.get().switchTo(nextMission);
        }
    }

    @Override
    public void exit() {
        if (window != null) {
            gui.removeWindow(window);
        }
    }
}
