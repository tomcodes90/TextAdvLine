package battle.actions;

import battle.TurnManager;
import characters.Entity;
import util.DeveloperLogger;
import util.PlayerLogger;

public class FleeAction implements BattleAction {
    private final Entity player;
    private final TurnManager manager;

    public FleeAction(Entity player, TurnManager manager) {
        this.player = player;
        this.manager = manager;
    }

    @Override
    public String name() {
        return "Flee";
    }

    @Override
    public void execute() {
        DeveloperLogger.log("[Battle] " + player.getName() + " attempts to flee...");
        PlayerLogger.log(player.getName() + " attempts to flee...");

        if (Math.random() < 0.5) {
            PlayerLogger.log("Successfully fled!");
            DeveloperLogger.log("[Battle] Flee succeeded");
            manager.setBattleOver(true);
        } else {
            PlayerLogger.log("Couldn't escape!");
            DeveloperLogger.log("[Battle] Flee failed");
        }
    }
}
