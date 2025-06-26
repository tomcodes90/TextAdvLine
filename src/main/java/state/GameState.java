package state;

import characters.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import scenes.missions.MissionType;

import java.io.File;
import java.io.IOException;

@Setter
@Getter
public class GameState {
    private static final GameState INSTANCE = new GameState();

    private Player player;
    private MissionType missionFlag;

    @JsonIgnore // Don’t serialize singleton
    public static GameState get() {
        return INSTANCE;
    }

    private GameState() {
    }

    // ========== SAVE / LOAD ==========

    public boolean saveToFile() {
        try {
            String userHome = System.getProperty("user.home");
            File saveDir = new File(userHome, ".textadv/saves");
            if (!saveDir.exists()) saveDir.mkdirs();

            File saveFile = new File(saveDir, "save.json");
            new ObjectMapper().writeValue(saveFile, this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loadInstance() {
        try {
            String userHome = System.getProperty("user.home");
            File saveFile = new File(userHome, ".textadv/saves/save.json");

            if (saveFile.exists()) {
                GameState loaded = new ObjectMapper().readValue(saveFile, GameState.class);
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
