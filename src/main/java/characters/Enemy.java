package characters;

import items.Consumable;

public class Enemy extends Entity {
    private final StatsType statBoost;

    public Enemy(String name, StatsType statBoost, int level) {
        super(name);
        this.statBoost = statBoost;
        simulateLevelUp(level);
    }

    private void simulateLevelUp(int levels) {
        stats.forEach((statsType, value) -> {
            int increase = (statBoost == statsType) ? 5 * levels : 2 * levels;
            stats.put(statsType, value + increase);
        });
    }

    @Override
    public void assignConsumableToSlot(Consumable consumable, int index) {

    }
}
