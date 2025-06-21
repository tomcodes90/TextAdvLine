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
        if (Math.random() < 0.5) {
            PlayerLogger.log("✅ Successfully fled!");
            manager.onPlayerFlee();              // <-- new helper
        } else {
            PlayerLogger.log("❌ Couldn't escape!");
        }
    }
}
