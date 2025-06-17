package items;

import characters.Entity;
import characters.StatsType;

public class StatEnhancer extends Consumable {
    private final StatsType statToBoost;

    StatEnhancer(String id, String name, String description, int valueToApply, StatsType statToBoost) {
        super(id, name, description, valueToApply);
        this.statToBoost = statToBoost;
    }

    @Override
    public void use(Entity entity) {
       entity.setStat(statToBoost, ++valueToApply);
    }

    public StatsType getStatToBoost() {
        return statToBoost;
    }
}
