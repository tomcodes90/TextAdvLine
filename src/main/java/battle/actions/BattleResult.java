package battle.actions;

/**
 * Enum: BattleResult
 * <p>
 * Purpose:
 * Represents the possible outcomes of a battle.
 * Used to determine post-combat flow (e.g., reward screen, game over, escape).
 */
public enum BattleResult {
    /**
     * The player has won the battle
     */
    VICTORY,

    /**
     * The player has been defeated
     */
    DEFEAT,

    /**
     * The player successfully fled from combat
     */
    FLED
}
