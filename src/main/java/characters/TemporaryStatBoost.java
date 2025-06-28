package characters;

import characters.Entity;
import characters.StatsType;

/**
 * Class: TemporaryStatBoost
 * <p>
 * Purpose:
 * Represents a temporary stat modifier (buff) applied to an Entity (Player or Enemy).
 * Automatically applies when created and reverts itself after a set number of turns.
 * <p>
 * Example: A +5 Strength buff for 3 turns.
 */
public class TemporaryStatBoost {

    /**
     * The stat being boosted (e.g., STRENGTH, DEFENSE)
     */
    private final StatsType stat;

    /**
     * The amount of the boost
     */
    private final int boostAmount;

    /**
     * Number of turns remaining before the boost wears off
     */
    private int turnsLeft;

    /**
     * The entity this boost is applied to
     */
    private final Entity entity;

    /**
     * Constructor: Applies the boost immediately upon creation.
     *
     * @param entity      The entity receiving the boost
     * @param stat        The stat being boosted
     * @param boostAmount The amount to increase the stat
     * @param duration    How many turns the boost lasts
     */
    public TemporaryStatBoost(Entity entity, StatsType stat, int boostAmount, int duration) {
        this.entity = entity;
        this.stat = stat;
        this.boostAmount = boostAmount;
        this.turnsLeft = duration;
        applyBoost(); // Apply boost on creation
    }

    /**
     * Applies the boost to the entity's stat
     */
    private void applyBoost() {
        entity.modifyStat(stat, boostAmount);
    }

    /**
     * Called each turn. Decreases duration and removes the boost when expired.
     */
    public void tick() {
        turnsLeft--;
        if (turnsLeft <= 0) {
            entity.modifyStat(stat, -boostAmount); // Revert boost
            entity.removeTemporaryBoost(this);     // Remove from active list
        }
    }
}
