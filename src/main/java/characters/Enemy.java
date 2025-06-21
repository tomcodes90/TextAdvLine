package characters;

import items.Consumable;
import items.Item;
import lombok.Getter;

import java.util.List;

@Getter
public class Enemy extends Entity {

    private final StatsType statBoost;
    private final List<Item> lootReward;
    private final int expReward;
    private final AIRole aiRole;


    public Enemy(String name, StatsType statBoost, int level, List<Item> lootReward, int expReward, AIRole aiRole) {
        super(name);
        this.statBoost = statBoost;
        this.lootReward = lootReward;
        this.expReward = expReward;
        this.aiRole = aiRole;
        levelUpTo(level);
    }

    private void levelUpTo(int level) {
        getStats().forEach((type, value) -> {
            int increase = (statBoost == type) ? 5 * level - 1 : 2 * level - 1;
            getStats().put(type, value + increase);
        });
    }

    @Override
    public void assignConsumableToSlot(Consumable consumable, int index) {
        // Enemies don't use consumables
    }
}
