package battle.actions;

import battle.TurnManager;
import characters.Entity;
import util.DeveloperLogger;
import util.PlayerLogger;

/**
 * Class: FleeAction
 * <p>
 * Purpose:
 * Represents the player's attempt to flee from battle.
 * Has a 50% success chance. On success, calls `TurnManager.onPlayerFlee()` to end the battle.
 */
public class FleeAction implements BattleAction {

    /**
     * Turn manager that controls the battle flow
     */
    private final TurnManager manager;

    /**
     * Constructor
     *
     * @param manager The battle's TurnManager instance
     */
    public FleeAction(TurnManager manager) {
        this.manager = manager;
    }

    /**
     * Returns the name of the action for UI or logs.
     */
    @Override
    public String name() {
        return "Flee";
    }

    /**
     * Attempts to flee. 50% chance of success.
     * If successful, logs the escape and informs the turn manager.
     */
    @Override
    public void execute() {
        if (Math.random() < 0.5) {
            PlayerLogger.log("Successfully fled!");
            manager.onPlayerFlee(); // Ends the battle with FLED result
        } else {
            PlayerLogger.log("Couldn't escape!");
        }
    }
}
