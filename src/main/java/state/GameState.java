package state;

import characters.Player;
import items.MissionType;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

@Setter
@Getter
public class GameState {
    private static final GameState INSTANCE = new GameState();

    private Player player;

    private MissionType missionFlag;

    private GameState() {}

    public static GameState get() {
        return INSTANCE;
    }

    public void completeMission(MissionType mission) {
 missionFlag = mission;
    }
    // You can also store:
    // - Inventory
    // - Flags for missions completed
    // - Gold, location, party, etc.
}
