package missions;

import game.GameState;

public interface Mission {
    String getMissionName();
    void start(GameState gameState);
}
