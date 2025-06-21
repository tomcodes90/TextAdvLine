package items;

import characters.Entity;
import characters.StatsType;
import characters.TemporaryStatBoost;
import lombok.Getter;

@Getter
public class StatEnhancer extends Consumable {
    private final StatsType statToBoost;
    private final int length;

    StatEnhancer(String id, String name, String description, int pointsToApply, StatsType statToBoost, int length) {
        super(id, name, description, pointsToApply);
        this.statToBoost = statToBoost;
        this.length = length;
    }

    @Override
    public void use(Entity entity) {
        TemporaryStatBoost boost = new TemporaryStatBoost(entity, statToBoost, pointsToApply, length);
        entity.addTemporaryBoost(boost);
    }

}
