package battle.actions;

import battle.BattleSystem;
import characters.Entity;

/**
 * Class: AttackAction
 * <p>
 * Purpose:
 * Represents a basic physical attack action in combat.
 * Implements the BattleAction interface and delegates the logic to BattleSystem.
 */
public class AttackAction implements BattleAction {

    /**
     * The entity performing the attack
     */
    private final Entity attacker;

    /**
     * The target entity being attacked
     */
    private final Entity target;

    /**
     * Constructor
     *
     * @param attacker The entity initiating the attack
     * @param target   The entity receiving the attack
     */
    public AttackAction(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    /**
     * Returns the name of the action (used in logs or UI)
     */
    @Override
    public String name() {
        return "Attack";
    }

    /**
     * Executes the attack action by calling the BattleSystem logic
     */
    @Override
    public void execute() {
        BattleSystem.attack(attacker, target);
    }
}
