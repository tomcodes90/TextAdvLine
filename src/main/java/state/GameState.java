package state;

import characters.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import scenes.missions.MissionType;

import java.io.File;
import java.io.IOException;

/**
 * ===============================
 * GameState
 * ===============================
 * <p>
 * Purpose:
 * This singleton holds the current state of the game: the active player and the current mission progress.
 * It can save this data to disk in JSON format and load it back later to resume gameplay.
 * <p>
 * Design:
 * - Singleton Pattern: Only one GameState exists at runtime.
 * - Uses Jackson's ObjectMapper to read/write a save.json file in the user's home folder.
 * <p>
 * Fields:
 * - player       → the current player with stats, spells, inventory, etc.
 * - missionFlag  → tracks game progression (which mission we’re on)
 * <p>
 * Save location:
 * ~/.textadv/saves/save.json   ← (auto-created if it doesn't exist)
 * <p>
 * keynotes:
 * - @JsonIgnore ensures singleton methods aren't serialized.
 * - saveToFile() and loadInstance() are the only methods needed for persistence.
 */
@Setter
@Getter
public class GameState {
    private static final GameState instance = new GameState();

    private Player player;
    private MissionType missionFlag;

    /**
     * Static accessor for the singleton instance.
     * Marked with @JsonIgnore to prevent serialization.
     */
    @JsonIgnore
    public static GameState get() {
        return instance;
    }

    // Private constructor to enforce singleton
    private GameState() {
    }

    // ==========================
    //        SAVE METHOD
    // ==========================

    /**
     * Saves the current game state to a file called `save.json`
     * under ~/.textadv/saves/
     * <p>
     * Returns true if successful, false otherwise.
     */
    public boolean saveToFile() {
        try {
            String userHome = System.getProperty("user.home");
            File saveDir = new File(userHome, ".textadv/saves");
            if (!saveDir.exists()) saveDir.mkdirs();  // Create folder if missing

            File saveFile = new File(saveDir, "save.json");
            new ObjectMapper().writeValue(saveFile, this); // Serialize this GameState to JSON
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================
    //        LOAD METHOD
    // ==========================

    /**
     * Loads the saved game state from ~/.textadv/saves/save.json
     * and injects it into the current singleton.
     * <p>
     * Returns true if successful, false otherwise.
     */
    public static boolean loadInstance() {
        try {
            String userHome = System.getProperty("user.home");
            File saveFile = new File(userHome, ".textadv/saves/save.json");

            if (saveFile.exists()) {
                GameState loaded = new ObjectMapper().readValue(saveFile, GameState.class);

                // Replace the internal data of the singleton instance
                GameState singleton = GameState.get();
                singleton.setPlayer(loaded.getPlayer());
                singleton.setMissionFlag(loaded.getMissionFlag());

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
