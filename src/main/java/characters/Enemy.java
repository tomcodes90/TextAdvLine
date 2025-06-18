package characters;

import items.Consumable;
import items.Item;
import lombok.Getter;

import java.util.List;

@Getter
public class Enemy extends Entity {

    private final StatsType statBoost;
    private final AIRole aiRole;
    private final List<Item> itemRewards;
    private final int expReward;

    public Enemy(String name, StatsType statBoost, int level, AIRole aiRole, List<Item> itemRewards, int expReward) {
        super(name);
        this.statBoost = statBoost;
        this.aiRole = aiRole;
        this.itemRewards = itemRewards;
        this.expReward = expReward;
        simulateLevelUp(level);
    }

    private void simulateLevelUp(int levels) {
        stats.forEach((type, value) -> {
            int increase = (statBoost == type) ? 5 * levels : 2 * levels;
            stats.put(type, value + increase);
        });
    }

    @Override
    public void assignConsumableToSlot(Consumable consumable, int index) {
        // Enemies don't use consumables
    }
}
