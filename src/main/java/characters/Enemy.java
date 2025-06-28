package characters;

import items.consumables.Consumable;
import items.Item;
import lombok.Getter;

import java.util.List;

/**
 * Class: Enemy
 * <p>
 * Purpose:
 * Represents an enemy unit in the game.
 * Inherits from Entity and includes additional logic for:
 * - AI behavior (via AIRole)
 * - Loot and rewards
 * - Stat scaling based on level
 */
@Getter
public class Enemy extends Entity {

    /**
     * The stat that grows faster when the enemy levels up
     */
    private final StatsType statBoost;

    /**
     * Loot dropped when defeated
     */
    private final List<Item> lootReward;

    /**
     * EXP awarded to the player upon defeat
     */
    private final int expReward;

    /**
     * Gold awarded to the player upon defeat
     */
    private final int goldReward;

    /**
     * AI behavior logic for this enemy
     */
    private final AIRole aiRole;

    /**
     * Constructor
     *
     * @param name       Enemy name
     * @param statBoost  Stat that increases more per level (e.g., STRENGTH)
     * @param level      Enemy level
     * @param lootReward List of items dropped on defeat
     * @param expReward  EXP reward value
     * @param goldReward Gold reward value
     * @param aiRole     AI behavior role (from AIRole enum)
     */
    public Enemy(String name, StatsType statBoost, int level, List<Item> lootReward,
                 int expReward, int goldReward, AIRole aiRole) {
        super(name);
        this.statBoost = statBoost;
        this.lootReward = lootReward;
        this.expReward = expReward;
        this.goldReward = goldReward;
        this.aiRole = aiRole;
        scaleStatsTo(level); // Scale stats to given level
    }

    /**
     * Scale stats to the enemy level.
     * The boosted stat receives higher growth.
     */
    private void scaleStatsTo(int level) {
        getStats().forEach((type, value) -> {
            int increase = (statBoost == type) ? 5 * level - 1 : 2 * level - 1;
            getStats().put(type, value + increase);
        });
    }

    /**
     * Consumables can be assigned here if needed.
     * Currently unused, but required by superclass (Entity).
     */
    @Override
    public void assignConsumableToSlot(Consumable consumable, int index) {
        // Not implemented for now — could be used for AI behavior in future
    }
}
