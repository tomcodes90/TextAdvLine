package battle.actions;

/**
 * ==========================================================
 * BattleAction Interface
 * ==========================================================
 * <p>
 * Purpose:
 * Represents a single action that can be taken during battle,
 * such as attacking, casting a spell, using an item, etc.
 * <p>
 * All battle actions must:
 * - Provide a name (for display in menus, logs, etc.)
 * - Execute themselves (perform the logic)
 */
public interface BattleAction {

    /**
     * Returns the name of the action.
     * This is used for menus and log messages.
     */
    String name();

    /**
     * Executes the action.
     * This will modify the battle state accordingly.
     */
    void execute();
}
