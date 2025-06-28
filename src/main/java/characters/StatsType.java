package characters;

/**
 * Enum: StatsType
 * <p>
 * Purpose:
 * Represents the different character stats used in the game.
 * These are used for both Player and Enemy entities to track combat-relevant attributes.
 * <p>
 * Can be used as keys in stat maps (e.g., EnumMap<StatsType, Integer>).
 */
public enum StatsType {
    /**
     * Current Health Points
     */
    HP,

    /**
     * Maximum Health Points
     */
    MAX_HP,

    /**
     * Physical attack strength
     */
    STRENGTH,

    /**
     * Magical power, influences spell damage
     */
    INTELLIGENCE,

    /**
     * Physical defense stat, used to reduce damage
     */
    DEFENSE,

    /**
     * Determines turn order in battle
     */
    SPEED,
}
